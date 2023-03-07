package com.ReRollBag.integration;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.BagsService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(RestDocumentationExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersIntegrationTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BagsRepository bagsRepository;

    @Autowired
    private BagsService bagsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BagsCount bagsCount;

    @BeforeAll
    void tearDown() {
        bagsRepository.deleteAll();
        usersRepository.deleteAll();
        bagsCount.tearDownMap();
    }

    @BeforeEach
    void setUpMockMvcForRestDocsAndSpringSecurity(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUpDefaultUsersAndAdmin() {
        Users defaultUsers = Users.builder()
                .UID("testUID")
                .usersId("testUsersId")
                .userRole(UserRole.ROLE_USER)
                .name("testUser")
                .build();

        Users admin = Users.builder()
                .UID("testAdminUID")
                .usersId("testAdminUsersId")
                .userRole(UserRole.ROLE_ADMIN)
                .name("testAdmin")
                .build();

        usersRepository.save(defaultUsers);
        usersRepository.save(admin);
    }

//    @Test
//    @Order(1)
//    @DisplayName("[Integration] 회원가입 후 즉시 로그인 및 토큰 리턴 테스트")
//    @Rollback(value = false)
//    void Integration_회원가입후_즉시로그인및_발급된토큰으로_dummyMethod_성공() throws Exception {
//        //given
//        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
//                "test@gmail.com",
//                "testNickname",
//                "testPassword",
//                null
//        );
//
//        UsersResponseDto responseDto = new UsersResponseDto("test@gmail.com", "testNickname");
//
//        //when
//        MvcResult saveResult = mockMvc.perform(post("/api/v2/users/save")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                )
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("Users-save",
//                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
//                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
//                        requestFields(
//                                fieldWithPath("usersId").description("usersID value to save.").type(JsonFieldType.STRING),
//                                fieldWithPath("nickname").description("nickname value to save.").type(JsonFieldType.STRING),
//                                fieldWithPath("idToken").description("idToken for verity Token valie.").type(JsonFieldType.STRING),
//                                fieldWithPath("userRole").description("userRole value to save. If null will return default value.").type(JsonFieldType.NULL)
//                        ),
//                        responseFields(
//                                fieldWithPath("accessToken").description("User's access token value").type(JsonFieldType.STRING),
//                                fieldWithPath("refreshToken").description("User's refresh token value").type(JsonFieldType.STRING)
//                        )
//                ))
//                .andReturn();
//
//        String content = saveResult.getResponse().getContentAsString();
//        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
//
//        String accessToken = loginResponseDto.getAccessToken();
//        String refreshToken = loginResponseDto.getRefreshToken();
//
//        mockMvc.perform(get("/api/v1/users/dummyMethod")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Token", accessToken))
//                //then
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("[Integration] 회원가입 후 같은 정보로 한번 더 회원가입 실패")
//    void Integration_회원가입후_같은정보로_한번더회원가입_실패() throws Exception {
//        //given
//        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
//                "test@gmail.com",
//                "testNickname",
//                "testPassword",
//                null
//        );
//
//        ErrorJson errorJson = ErrorJson.builder()
//                .errorCode(ErrorCode.DuplicateUserSaveException.getErrorCode())
//                .message("DuplicateUserSaveException")
//                .build();
//
//        //when
//        MvcResult saveResult = mockMvc.perform(post("/api/v2/users/save")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                )
//                //then
//                .andExpect(status().isForbidden())
//                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
//                .andDo(document("Users-save-DuplicatedUserSaveException",
//                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
//                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
//                        requestFields(
//                                fieldWithPath("usersId").description("Duplicated usersID").type(JsonFieldType.STRING),
//                                fieldWithPath("nickname").description("Duplicated nickname").type(JsonFieldType.STRING),
//                                fieldWithPath("idToken").description("idToken value to save.").type(JsonFieldType.STRING),
//                                fieldWithPath("userRole").description("userRole value to save. If null will return default value.").type(JsonFieldType.NULL)
//                        ),
//                        responseFields(
//                                fieldWithPath("errorCode").description("errorCode of DuplicatedUserSaveException").type(JsonFieldType.NUMBER),
//                                fieldWithPath("message").description("message of DuplicatedUserSaveException").type(JsonFieldType.STRING)
//                        )))
//                .andReturn();
//
//    }
//
//    @Test
//    @DisplayName("[Integration] 로그인 및 Redis 토큰 검증")
//    void Integration_로그인_테스트() throws Exception {
//        //given
//        UsersLoginRequestDto requestDto = new UsersLoginRequestDto(
//                "test@gmail.com",
//                "testPassword"
//        );
//
//        //when
//        MvcResult loginResult = mockMvc.perform(post("/api/v2/users/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                )
//                //then
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("Users-login",
//                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
//                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
//                        requestFields(
//                                fieldWithPath("usersId").description("usersId value for login").type(JsonFieldType.STRING),
//                                fieldWithPath("idToken").description("idToken value for login.").type(JsonFieldType.STRING)
//                        ),
//                        responseFields(
//                                fieldWithPath("accessToken").description("User's access token value").type(JsonFieldType.STRING),
//                                fieldWithPath("refreshToken").description("User's refresh token value").type(JsonFieldType.STRING)
//                        )))
//                .andReturn();
//
//        //when
//        String content = loginResult.getResponse().getContentAsString();
//        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
//        String expectedAccessToken = loginResponseDto.getAccessToken();
//        String expectedRefreshToken = loginResponseDto.getRefreshToken();
//        String actualAccessToken = redisService.findAccessToken("test@gmail.com");
//        String actualRefreshToken = redisService.findRefreshToken("test@gmail.com");
//
//        //then
//        assertThat(expectedAccessToken).isEqualTo(actualAccessToken);
//        assertThat(expectedRefreshToken).isEqualTo(actualRefreshToken);
//    }

//    @Test
//    @DisplayName("[Integration] 잘못된 ID 또는 Pw로 로그인 시도 예외")
//    void Integration_잘못된IDPW_로그인_예외_테스트() throws Exception {
//        //given
//        UsersLoginRequestDto requestDto = new UsersLoginRequestDto("invalidUsersId@gmail.com", "invalidUsersPassword");
//
//        ErrorJson errorJson = ErrorJson.builder()
//                .errorCode(ErrorCode.UsersIdOrPasswordInvalidException.getErrorCode())
//                .message("UsersIdOrPasswordInvalidException")
//                .build();
//
//        //when
//        mockMvc.perform(post("/api/v2/users/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                )
//                //then
//                .andExpect(status().isForbidden())
//                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
//                .andDo(print())
//                .andDo(document("Users-login-UsersIdOrPasswordInvalidException",
//                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
//                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
//                        requestFields(
//                                fieldWithPath("usersId").description("usersId value for login").type(JsonFieldType.STRING),
//                                fieldWithPath("idToken").description("idToken value for login.").type(JsonFieldType.STRING)
//                        ),
//                        responseFields(
//                                fieldWithPath("errorCode").description("errorCode of DuplicatedUserSaveException").type(JsonFieldType.NUMBER),
//                                fieldWithPath("message").description("message of DuplicatedUserSaveException").type(JsonFieldType.STRING)
//                        )))
//                .andReturn();
//    }

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
                    .andDo(document("Users-checkUserExist-True",
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
        String usersId = "testUsersId";

        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(false)
                .build();

        //when
        try {
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v2/users/checkUserExist/{usersId}", usersId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                    .andDo(document("Users-checkUserExist-False",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            pathParameters(
                                    parameterWithName("usersId").description("usersId for checking duplication")
                            ),
                            responseFields(
                                    fieldWithPath("data").description("Result of checking duplication").type(JsonFieldType.BOOLEAN)
                            )
                    ));
            ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Integration] 일반 사용자 토큰으로 v1 더미 메소드 테스트")
    void Integration_일반사용자토큰으로_v1_dummyMethod_성공() throws Exception {
        //given
        Users defaultUsers = Users.builder()
                .UID("testUID")
                .usersId("testUsersId")
                .userRole(UserRole.ROLE_USER)
                .name("testUser")
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(defaultUsers.getUID());
        String refreshToken = jwtTokenProvider.createRefreshToken(defaultUsers.getUID());

        //when
        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken))
                //then
                .andExpect(status().isOk())
                .andDo(document("Users-TestingToken-WithUserRole-Withv1Method",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USER")
                        )
                ))
                .andReturn();

    }

    @Test
    @DisplayName("[Integration] 관리자 토큰으로 v1, v3 더미 메소드 테스트")
    void Integration_관리자계정으로_발급된토큰으로_v1및v3_dummyMethod_성공() throws Exception {
        //given
        Users admin = Users.builder()
                .UID("testAdminUID")
                .usersId("testAdminUsersId")
                .userRole(UserRole.ROLE_ADMIN)
                .name("testAdmin")
                .build();

        //when
        String accessToken = jwtTokenProvider.createAccessToken(admin.getUID());
        String refreshToken = jwtTokenProvider.createRefreshToken(admin.getUID());

        mockMvc.perform(get("/api/v3/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken))
                //then
                .andExpect(status().isOk())
                .andDo(document("Users-TestingToken-WithAdminRole-Withv3Method",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_ADMIN")
                        )
                ))
                .andDo(print());

        mockMvc.perform(get("/api/v1/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken))
                //then
                .andExpect(status().isOk())
                .andDo(document("Users-TestingToken-WithAdminRole-Withv1Method",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_ADMIN")
                        )
                ))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] 일반 사용자 토큰으로 v3 메소드 실패 테스트")
    void Integration_일반사용자토큰으로_v3_dummyMethod_실패() throws Exception {
        //given
        Users defaultUsers = Users.builder()
                .UID("testUID")
                .usersId("testUsersId")
                .userRole(UserRole.ROLE_USER)
                .name("testUser")
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(defaultUsers.getUID());

        //when
        mockMvc.perform(get("/api/v3/users/dummyMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken))
                //then
                .andExpect(status().isForbidden())
                .andDo(document("Users-TestingToken-WithUserRole-Withv3Method",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USERS")
                        )
                ))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] getRentingBagsList 테스트")
    @Rollback(value = false)
    void Integration_getRentingBagsList_테스트() throws Exception {
        //given
        Users users = Users.builder()
                .UID("testUID")
                .usersId("testUsersId")
                .userRole(UserRole.ROLE_USER)
                .name("testUser")
                .build();

        usersRepository.save(users);

        Users admin = Users.builder()
                .UID("testAdminUID")
                .usersId("testAdminUsersId")
                .userRole(UserRole.ROLE_ADMIN)
                .name("testAdmin")
                .build();

        usersRepository.save(admin);

        String accessToken = jwtTokenProvider.createAccessToken(users.getUID());
        String adminToken = jwtTokenProvider.createAccessToken(admin.getUID());

        BagsSaveRequestDto bagsSaveRequestDto = new BagsSaveRequestDto(
                "KOR",
                "SUWON"
        );

        BagsResponseDto bagsResponseDto1 = new BagsResponseDto(
                "KOR_SUWON_1",
                false,
                LocalDateTime.MIN.toString(),
                ""
        );

        BagsResponseDto bagsResponseDto2 = new BagsResponseDto(
                "KOR_SUWON_2",
                false,
                LocalDateTime.MIN.toString(),
                ""
        );

        // Save Bags first time
        mockMvc.perform(post("/api/v3/bags/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bagsSaveRequestDto))
                        .header("token", adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(bagsResponseDto1)));

        // Save Bags second time
        mockMvc.perform(post("/api/v3/bags/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bagsSaveRequestDto))
                        .header("token", adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(bagsResponseDto2)));
        ;

        BagsRentOrReturnRequestDto rentOrReturnRequestDto1 = new BagsRentOrReturnRequestDto(
                "testUsersId",
                "KOR_SUWON_1"
        );

        BagsRentOrReturnRequestDto rentOrReturnRequestDto2 = new BagsRentOrReturnRequestDto(
                "testUsersId",
                "KOR_SUWON_2"
        );

        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(true)
                .build();

        mockMvc.perform(post("/api/v2/bags/renting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(rentOrReturnRequestDto1))
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                .andReturn();

        mockMvc.perform(post("/api/v2/bags/renting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(rentOrReturnRequestDto2))
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                .andReturn();


        List<BagsResponseDto> expectedList = new ArrayList<>();
        Bags bags1 = bagsRepository.findById("KOR_SUWON_1").get();
        Bags bags2 = bagsRepository.findById("KOR_SUWON_2").get();
        expectedList.add(new BagsResponseDto(bags1));
        expectedList.add(new BagsResponseDto(bags2));
        Collections.sort(expectedList);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/users/getRentingBagsList/{usersId}", users.getUsersId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", accessToken))
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedList)))
                .andDo(document("Users-getRentingBagsList",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USERS")
                        ),
                        pathParameters(
                                parameterWithName("usersId").description("usersId for getRentingBagsList")
                        ),
                        responseFields(
                                fieldWithPath("[].bagsId").description("Bag's Id"),
                                fieldWithPath("[].whenIsRented").description("LocalDateTime's String when is rented"),
                                fieldWithPath("[].rentingUsersId").description("User's Id who rented"),
                                fieldWithPath("[].rented").description("True/False if rented or not")
                        )
                ))
                .andDo(print());
    }
}
