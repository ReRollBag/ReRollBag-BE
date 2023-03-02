package com.ReRollBag.integration;

import com.ReRollBag.auth.ExceptionHandlerFilter;
import com.ReRollBag.auth.JwtAuthenticationFilter;
import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Tokens.AccessTokenResponseDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.repository.AccessTokenRepository;
import com.ReRollBag.repository.RefreshTokenRepository;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.RedisService;
import com.ReRollBag.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(RestDocumentationExtension.class)
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

    @BeforeEach
    void setUpForSpringRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilters(new ExceptionHandlerFilter())
                .addFilters(new JwtAuthenticationFilter(jwtTokenProvider))
                .apply(springSecurity())
                .build();
    }

    @BeforeAll
    private void init_saveUsers() throws Exception {

        // Set jwtToken Valid Time
        jwtTokenProvider.setAccessTokenValidTime(1L);
        jwtTokenProvider.setRefreshTokenValidTime(5L);

        // Save default Users to Repository
        Users defaultUsers = Users.builder()
                .UID("testUID")
                .usersId("testUsersId")
                .userRole(UserRole.ROLE_USER)
                .name("testUser")
                .build();
        usersRepository.save(defaultUsers);

        // Save default accessToken and RefreshToken
        accessToken = jwtTokenProvider.createAccessToken(defaultUsers.getUID());
        refreshToken = jwtTokenProvider.createRefreshToken(defaultUsers.getUID());

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
                .andDo(document("Auth-TokenIsNullException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of TokenIsNullException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of TokenIsNullException").type(JsonFieldType.STRING)
                        )))
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
                .andDo(document("Auth-accessToken-SignatureException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("Invalid Access Token value")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of SignatureException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of SignatureException").type(JsonFieldType.STRING)
                        )
                ))
                .andReturn();

    }

    @Test
    @Order(3)
    @DisplayName("[Integration] Expired Access Token 보낼 때 예외 (httpStatus : 202 / AccessTokenExpiredException) 테스트")
    void Integration_Expired_AccessToken_테스트() throws Exception {
        // Access Token 의 만료를 위해 1초 대기
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
                .andDo(document("Auth-accessToken-ExpiredJwtException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("Expired access token value")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of ExpiredJwtException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of ExpiredJwtException").type(JsonFieldType.STRING)
                        )
                ))
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
                .andDo(document("Auth-reIssue",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("Refresh token value")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("New Access token value which is reIssued").type(JsonFieldType.STRING)
                        )
                ))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        AccessTokenResponseDto accessTokenResponseDto = new ObjectMapper().readValue(content, AccessTokenResponseDto.class);
        accessToken = accessTokenResponseDto.getAccessToken();


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
                .andDo(document("Auth-reIssue-SignatureException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("Invalid refresh token value")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of TokenIsNullException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of TokenIsNullException").type(JsonFieldType.STRING)
                        )
                ))
                .andReturn();

    }

    @Test
    @Order(6)
    @DisplayName("[Integration] Refresh Token 만료 이후 ReIssue 예외 (httpStatus : 202 / RefreshTokenExpiredException) 테스트")
    void Integration_Expired_RefreshToken_테스트() throws Exception {
        // Refresh Token 이 Expired 될 때 까지 대기
        Thread.sleep(5 * 1000L);
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
                .andDo(document("Auth-reIssue-ExpiredJwtException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("Expired refresh token value")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of TokenIsNullException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of TokenIsNullException").type(JsonFieldType.STRING)
                        )
                ))
                .andReturn();
    }

//    @Test
//    @Order(7)
//    @DisplayName("[Integration] ReIssue 시도 시 RefreshToken 의 ExpireTime 연장 테스트")
//    void Integration_RefreshToken_Automatically_Increase_ExpireTime() throws Exception {
//        // Create New AccessToken and RefreshToken
//        String UID = "testUID";
//        accessToken = jwtTokenProvider.createAccessToken(UID);
//        refreshToken = jwtTokenProvider.createRefreshToken(UID);
//
//        // Wait Until AccessToken Expire
//        Thread.sleep(3*1000L);
//
//        // ReIssue
//        mockMvc.perform(post("/api/v2/users/reIssue")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Token", refreshToken)
//        )
//                .andReturn();
//
//        // Wait Until Old RefreshToken Expire
//        Thread.sleep(2*1000L);
//
//        // Try ReIssue and Check If it is Able to use; not Expired
//        mockMvc.perform(post("/api/v2/users/reIssue")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Token", refreshToken)
//                )
//                .andExpect(status().isOk())
//                .andReturn();
//    }
}
