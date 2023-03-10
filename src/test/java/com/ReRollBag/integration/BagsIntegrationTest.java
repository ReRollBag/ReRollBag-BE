package com.ReRollBag.integration;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.BagsService;
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

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BagsIntegrationTest {

    @Autowired
    private BagsService bagsService;

    @Autowired
    private BagsRepository bagsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BagsCount bagsCount;

    @Autowired
    private MockMvc mockMvc;

    private String usersToken;
    private String adminToken;

    @BeforeAll
    void teardown() {
        bagsRepository.deleteAll();
        usersRepository.deleteAll();
        bagsCount.tearDownMap();
        jwtTokenProvider.setAccessTokenValidTime(100000L);
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
    void saveUsersAndAdminAndSetUpTokens() throws Exception {
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

        usersToken = jwtTokenProvider.createAccessToken(defaultUsers.getUID(), defaultUsers.getUsersId());
        adminToken = jwtTokenProvider.createAccessToken(admin.getUID(), admin.getUsersId());

    }

    @Test
    @DisplayName("[Integration] ?????? ??????")
    @Rollback(value = false)
    @Order(value = 1)
    void Integration_?????????????????????_????????????() throws Exception {
        //given
        BagsSaveRequestDto bagsSaveRequestDto = new BagsSaveRequestDto(
                "KOR",
                "SUWON"
        );

        BagsResponseDto bagsResponseDto = new BagsResponseDto(
                "KOR_SUWON_1",
                false,
                LocalDateTime.MIN.toString(),
                ""
        );

        //when
        MvcResult saveResult = mockMvc.perform(post("/api/v3/bags/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bagsSaveRequestDto))
                        .header("token", adminToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(bagsResponseDto)))
                .andDo(document("Bags-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_ADMIN")
                        ),
                        requestFields(
                                fieldWithPath("countryCode").description("Code of country").type(JsonFieldType.STRING),
                                fieldWithPath("regionCode").description("Code of region").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("bagsId").description("Generated bagsId. {countryCode}_{regionCode}_{auto_increment_index}").type(JsonFieldType.STRING),
                                fieldWithPath("rented").description("If bag is rented. Default is false.").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("whenIsRented").description("LocalDateTime of rented. If it is not renting, value is LocalDateTime.MIN").type(JsonFieldType.STRING),
                                fieldWithPath("rentingUsersId").description("Id which users is renting. If it is not renting, value is empty String").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("[Integration] ?????? ?????? ??? ???????????? index ?????? ?????????")
    @Order(value = 2)
    void Integration_?????????????????????_?????????????????????_index??????_?????????() throws Exception {
        //given
        BagsSaveRequestDto bagsSaveRequestDto = new BagsSaveRequestDto(
                "KOR",
                "SUWON"
        );

        BagsResponseDto bagsResponseDto = new BagsResponseDto(
                "KOR_SUWON_2",
                false,
                LocalDateTime.MIN.toString(),
                ""
        );

        //when
        MvcResult saveResult = mockMvc.perform(post("/api/v3/bags/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bagsSaveRequestDto))
                        .header("token", adminToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(bagsResponseDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("[Integration] ?????? ?????? ?????????")
    @Rollback(value = false)
    @Order(value = 3)
    void Integration_????????????_?????????() throws Exception {
        //given
        BagsRentOrReturnRequestDto rentOrReturnRequestDto = new BagsRentOrReturnRequestDto(
                "testUsersId",
                "KOR_SUWON_1"
        );

        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(true)
                .build();

        //when
        mockMvc.perform(post("/api/v2/bags/renting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(rentOrReturnRequestDto))
                        .header("token", usersToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                .andDo(document("Bags-Renting",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USERS (ADMIN also can do)")
                        ),
                        requestFields(
                                fieldWithPath("usersId").description("usersId who rent bags"),
                                fieldWithPath("bagsId").description("bagsId for renting")
                        ),
                        responseFields(
                                fieldWithPath("data").description("result of renting")
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] ?????? ?????? ?????? ?????? ??? ?????? (httpStatus : 400 / AlreadyRentedException) ?????????")
    @Order(value = 4)
    void Integration_??????????????????_?????????() throws Exception {
        //given
        BagsRentOrReturnRequestDto rentOrReturnRequestDto = new BagsRentOrReturnRequestDto(
                "testUsersId",
                "KOR_SUWON_1"
        );

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(4001)
                .message("AlreadyRentedException")
                .build();

        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(true)
                .build();

        //when
        mockMvc.perform(post("/api/v2/bags/renting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(rentOrReturnRequestDto))
                        .header("token", usersToken)
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
                .andDo(document("Bags-Renting-AlreadyRentedException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USERS (ADMIN also can do)")
                        ),
                        requestFields(
                                fieldWithPath("usersId").description("usersId who rent bags"),
                                fieldWithPath("bagsId").description("bagsId for renting")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of AlreadyRentedException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of AlreadyRentedException").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] ?????? ?????? ?????? ????????? ?????? (httpStatus : 400 / ReturnRequestUserMismatchException) ?????????")
    @Order(value = 5)
    void Integration_??????????????????_?????????????????????_?????????() throws Exception {
        //given
        BagsRentOrReturnRequestDto rentOrReturnRequestDto = new BagsRentOrReturnRequestDto(
                "testAdminUsersId",
                "KOR_SUWON_1"
        );

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(4000)
                .message("ReturnRequestUserMismatchException")
                .build();

        //when
        mockMvc.perform(post("/api/v2/bags/requestReturning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(rentOrReturnRequestDto))
                        .header("token", usersToken)
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)))
                .andDo(document("Bags-RequestReturning-ReturnRequestUserMismatchException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USERS (ADMIN also can do)")
                        ),
                        requestFields(
                                fieldWithPath("usersId").description("usersId with inconsistent renting users' id"),
                                fieldWithPath("bagsId").description("bagsId for rent")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("errorCode of ReturnRequestUserMismatchException").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("message of ReturnRequestUserMismatchException").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] ?????? ?????? ?????? ?????????")
    @Order(value = 6)
    void Integration_??????????????????_?????????() throws Exception {
        //given
        BagsRentOrReturnRequestDto rentOrReturnRequestDto = new BagsRentOrReturnRequestDto(
                "testUsersId",
                "KOR_SUWON_1"
        );

        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(true)
                .build();

        //when
        mockMvc.perform(post("/api/v2/bags/requestReturning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(rentOrReturnRequestDto))
                        .header("token", usersToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                .andDo(document("Bags-RequestReturning",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USERS (ADMIN also can do)")
                        ),
                        requestFields(
                                fieldWithPath("usersId").description("usersId who rent bags"),
                                fieldWithPath("bagsId").description("bagsId for rent")
                        ),
                        responseFields(
                                fieldWithPath("data").description("result of rent")
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] ?????? ?????? ?????????")
    @Order(value = 7)
    void Integration_????????????_?????????() throws Exception {
        //given
        String bagsId = "KOR_SUWON_1";

        MockResponseDto expectedResponseDto = MockResponseDto.builder()
                .data(true)
                .build();

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v3/bags/returning/{bagsId}", bagsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", adminToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponseDto)))
                .andDo(document("Bags-Returning",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_ADMIN (ADMIN only can do)")
                        ),
                        pathParameters(
                                parameterWithName("bagsId").description("BagsId for returning")
                        ),
                        responseFields(
                                fieldWithPath("data").description("result of rent")
                        )))
                .andDo(print());
    }

}

