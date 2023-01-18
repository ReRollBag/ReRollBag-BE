package com.ReRollBag.integration;

import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Users.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.Users.UsersResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.exceptions.ErrorCode;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(RestDocumentationExtension.class)
public class UsersIntegrationTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUpForSpringRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }


    @Test
    @Order(1)
    @DisplayName("[Integration] 회원가입 후 즉시 로그인 및 토큰 리턴 테스트")
    @Rollback(value = false)
    void Integration_회원가입후_즉시로그인및_발급된토큰으로_dummyMethod_성공() throws Exception {
        //given
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );

        UsersResponseDto responseDto = new UsersResponseDto("test@gmail.com", "testNickname");

        //when
        MvcResult saveResult = mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Users-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("usersId").description("usersID value to save.").type(JsonFieldType.STRING),
                                fieldWithPath("nickname").description("nickname value to save.").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("password value to save.").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("User's access token value").type(JsonFieldType.STRING),
                                fieldWithPath("refreshToken").description("User's refresh token value").type(JsonFieldType.STRING)
                        )
                ))
                .andReturn();

        String content = saveResult.getResponse().getContentAsString();
        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);

        String accessToken = loginResponseDto.getAccessToken();
        String refreshToken = loginResponseDto.getRefreshToken();

        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken))
                //then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[Integration] 회원가입 후 같은 정보로 한번 더 회원가입 실패")
    void Integration_회원가입후_같은정보로_한번더회원가입_실패() throws Exception {
        //given
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(ErrorCode.DuplicateUserSaveException.getErrorCode())
                .message("DuplicateUserSaveException")
                .build();

        //when
        MvcResult saveResult = mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                //then
                .andExpect(status().isForbidden())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
                .andDo(document("Users-save-DuplicatedUserSaveException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("usersId").description("Duplicated usersID").type(JsonFieldType.STRING),
                                fieldWithPath("nickname").description("Duplicated nickname").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("password value to save.").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of DuplicatedUserSaveException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of DuplicatedUserSaveException").type(JsonFieldType.STRING)
                        )))
                .andReturn();

    }

    @Test
    @DisplayName("[Integration] 로그인 및 Redis 토큰 검증")
    void Integration_로그인_테스트() throws Exception {
        //given
        UsersLoginRequestDto requestDto = new UsersLoginRequestDto(
                "test@gmail.com",
                "testPassword"
        );

        //when
        MvcResult loginResult = mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Users-login",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("usersId").description("usersId value for login").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("password value for login").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("User's access token value").type(JsonFieldType.STRING),
                                fieldWithPath("refreshToken").description("User's refresh token value").type(JsonFieldType.STRING)
                        )))
                .andReturn();

        //when
        String content = loginResult.getResponse().getContentAsString();
        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
        String expectedAccessToken = loginResponseDto.getAccessToken();
        String expectedRefreshToken = loginResponseDto.getRefreshToken();
        String actualAccessToken = redisService.findAccessToken("test@gmail.com");
        String actualRefreshToken = redisService.findRefreshToken("test@gmail.com");

        //then
        assertThat(expectedAccessToken).isEqualTo(actualAccessToken);
        assertThat(expectedRefreshToken).isEqualTo(actualRefreshToken);
    }

    @Test
    @DisplayName("[Integration] 잘못된 ID 또는 Pw로 로그인 시도 예외")
    void Integration_잘못된IDPW_로그인_예외_테스트() throws Exception {
        //given
        UsersLoginRequestDto requestDto = new UsersLoginRequestDto("invalidUsersId@gmail.com", "invalidUsersPassword");

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(ErrorCode.UsersIdOrPasswordInvalidException.getErrorCode())
                .message("UsersIdOrPasswordInvalidException")
                .build();

        //when
        mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                //then
                .andExpect(status().isForbidden())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
                .andDo(print())
                .andDo(document("Users-login-UsersIdOrPasswordInvalidException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("usersId").description("usersId value for login").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("password value for login").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of DuplicatedUserSaveException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of DuplicatedUserSaveException").type(JsonFieldType.STRING)
                        )))
                .andReturn();
    }

    @Test
    @DisplayName("[Integration] 아이디 중복 검사 성공 case")
    void Integration_아이디_중복검사_성공() throws UsersIdAlreadyExistException {
        //given
        String usersId = "notDuplicatedUsersId@gmail.com";
        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(true)
                .build();

        //when
        try {
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v2/users/checkUserExist/{usersId}", usersId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                    .andDo(document("Users-checkUserExist",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            pathParameters(
                                    parameterWithName("usersId").description("usersId for checking duplication")
                            ),
                            responseFields(
                                    fieldWithPath("data").description("Result of checking duplication").type(JsonFieldType.BOOLEAN)
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Integration] 아이디 중복 검사 실패 case")
    void Integration_아이디_중복검사_실패() throws Exception {
        //given
        String usersId = "test@gmail.com";

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(1000)
                .message("UsersIdAlreadyExistException")
                .build();

        //when
        try {
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v2/users/checkUserExist/{usersId}", usersId))
                    //then
                    .andExpect(status().isAccepted())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
                    .andDo(document("Users-checkUserExist-UsersIdAlreadyExistException",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            pathParameters(
                                    parameterWithName("usersId").description("duplicated usersId")
                            ),
                            responseFields(
                                    fieldWithPath("errorCode").description("errorCode of UsersIdAlreadyExistException").type(JsonFieldType.NUMBER),
                                    fieldWithPath("message").description("message of UsersIdAlreadyExistException").type(JsonFieldType.STRING)
                            )
                    ));
            ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Integration] 올바른 로그인 로직 이후 dummyMethod 호출 성공 case")
    void Integration_로그인이후_발급된토큰으로_dummyMethod_성공() throws Exception {
        //given
        UsersLoginRequestDto requestDto = new UsersLoginRequestDto(
                "test@gmail.com",
                "testPassword"
        );

        //when
        MvcResult loginResult = mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
        //then
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn();

        //when
        String content = loginResult.getResponse().getContentAsString();
        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
        String accessToken = loginResponseDto.getAccessToken();
        String refreshToken = loginResponseDto.getRefreshToken();
        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken))
                //then
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @DisplayName("[Integration] 닉네임 중복 검사 성공 case")
    void Integration_닉네임_중복검사_성공() throws UsersIdAlreadyExistException {
        //given
        String nickname = "notDuplicatedNickname";
        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(true)
                .build();

        //when
        try {
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v2/users/checkNicknameExist/{nickname}", nickname))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                    .andDo(document("Users-checkNicknameExist",
                            pathParameters(
                                    parameterWithName("nickname").description("nickname for checking duplication")
                            ),
                            responseFields(
                                    fieldWithPath("data").description("Result of checking duplication").type(JsonFieldType.BOOLEAN)
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Integration] 닉네임 중복 검사 실패 case")
    void Integration_닉네임_중복검사_실패() throws Exception {
        //given
        String nickname = "testNickname";

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(1002)
                .message("NicknameAlreadyExistException")
                .build();

        //when
        try {
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v2/users/checkNicknameExist/{nickname}", nickname))
                    //then
                    .andExpect(status().isAccepted())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
                    .andDo(document("Users-checkNicknameExist-NicknameAlreadyExistException",
                            pathParameters(
                                    parameterWithName("nickname").description("duplicated nickname")
                            ),
                            responseFields(
                                    fieldWithPath("errorCode").description("errorCode of NicknameAlreadyExistException").type(JsonFieldType.NUMBER),
                                    fieldWithPath("message").description("message of NicknameAlreadyExistException").type(JsonFieldType.STRING)
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
