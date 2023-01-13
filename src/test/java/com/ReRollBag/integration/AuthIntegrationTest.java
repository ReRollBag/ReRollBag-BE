package com.ReRollBag.integration;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private MockMvc mockMvc;

    private String AccessToken;
    private String RefreshToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    private void init_saveUsers() throws Exception {

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
        AccessToken = loginResponseDto.getAccessToken();
        RefreshToken = loginResponseDto.getRefreshToken();

    }

    @AfterAll
    void tearDown() {
        usersRepository.deleteAll();
        accessTokenRepository.deleteAll();
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("[Integration] Token 값 Null 일때 예외 (httpStatus : 202) 테스트")
    void Integration_Token_null_테스트() throws Exception {
        //given
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(2002)
                .message("TokenIsNullException")
                .build();
        //when
        mockMvc.perform(get("/api/v1/users/dummyMethod")
                .contentType(MediaType.APPLICATION_JSON)
                )
        //then
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print())
                .andReturn();
    }

}
