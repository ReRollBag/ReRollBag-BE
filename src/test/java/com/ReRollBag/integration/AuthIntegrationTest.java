package com.ReRollBag.integration;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.UsersResponseDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.repository.AccessTokenRepository;
import com.ReRollBag.repository.RefreshTokenRepository;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.RedisService;
import com.ReRollBag.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {
    @Autowired
    private UsersService usersService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    private void init_saveUsers() throws Exception {

        // Set jwtToken Valid Time
        jwtTokenProvider.setAccessTokenValidTime(1L);
        jwtTokenProvider.setRefreshTokenValidTime(3L);

        UsersSaveRequestDto usersSaveRequestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );

        UsersResponseDto responseDto = new UsersResponseDto("test@gmail.com", "testNickname");

        mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usersSaveRequestDto))
                )
                .andDo(print());

        UsersLoginRequestDto usersLoginRequestDto = new UsersLoginRequestDto(
                "test@gmail.com",
                "testPassword"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usersLoginRequestDto))
                )
                .andDo(print())
                .andReturn();

        String content = loginResult.getResponse().getContentAsString();
        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
        accessToken = loginResponseDto.getAccessToken();
        refreshToken = loginResponseDto.getRefreshToken();

    }

    @AfterAll
    void tearDown() {
        usersRepository.deleteAll();
        accessTokenRepository.deleteAll();
        refreshTokenRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("[Integration] Token 값 Null 일때 예외 (httpStatus : 202 / TokenIsNullException) 테스트")
    void Integration_Token_null_테스트() throws Exception {
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(2002)
                .message("TokenIsNullException")
                .build();
        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @Order(2)
    @DisplayName("[Integration] 올바르지 않은 AccessToken 값 보낼 때 예외 (httpStatus : 403 / SignatureException) 테스트")
    void Integration_Invalid_AccessToken_테스트() throws Exception {
        String invalidAccessToken = "InvalidAccessToken";

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(2003)
                .message("SignatureException")
                .build();

        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", invalidAccessToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print())
                .andReturn();

    }

    @Test
    @Order(3)
    @DisplayName("[Integration] Expired Access Token 보낼 때 예외 (httpStatus : 202 / AccessTokenExpiredException) 테스트")
    void Integration_Expired_AccessToken_테스트() throws Exception {
        // Access Token 의 만료를 위해 2초 대기
        Thread.sleep(1000L);

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(2000)
                .message("ExpiredJwtException")
                .build();

        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken)
                )
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @Order(4)
    @DisplayName("[Integration] ReIssue 성공 테스트")
    void Integration_ReIssue_테스트() throws Exception {
        // Access Token 의 경우 만료, Refresh Token 의 경우 아직 Valid
        MvcResult mvcResult = mockMvc.perform(post("/api/v2/users/reIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", refreshToken)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        accessToken = mvcResult.getResponse().getContentAsString();

        // 새로 발급 받은 Access Token 이 정상 작동 하는 지 확인
        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(5)
    @Transactional
    @DisplayName("[Integration] 올바르지 않은 RefreshToken 값 보낼 때 예외 (httpStatus : 403 / SignatureException) 테스트")
    void Integration_Invalid_RefreshToken_테스트() throws Exception {
        // Access Token 이 Expired 될 때 까지 대기
        Thread.sleep(1 * 1000L);

        String invalidRefreshToken = "invalidRefreshToken";

        mockMvc.perform(post("/api/v2/users/reIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", invalidRefreshToken)
                )
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();

    }

    @Test
    @Order(6)
    @DisplayName("[Integration] Refresh Token 만료 이후 ReIssue 예외 (httpStatus : 202 / RefreshTokenExpiredException) 테스트")
    void Integration_Expired_RefreshToken_테스트() throws Exception {
        // Refresh Token 이 Expired 될 때 까지 대기
        Thread.sleep(1 * 1000L);
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(2000)
                .message("ExpiredJwtException")
                .build();

        mockMvc.perform(post("/api/v2/users/reIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", refreshToken)
                )
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print())
                .andReturn();
    }

}
