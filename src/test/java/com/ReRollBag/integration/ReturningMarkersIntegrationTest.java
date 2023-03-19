package com.ReRollBag.integration;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.ReturningMarkers.ReturningMarkersResponseDto;
import com.ReRollBag.domain.dto.ReturningMarkers.ReturningMarkersSaveRequestDto;
import com.ReRollBag.domain.entity.ReturningMarkers;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.repository.ReturningMarkersRepository;
import com.ReRollBag.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.client.methods.RequestBuilder.post;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class ReturningMarkersIntegrationTest {

    @Autowired
    private ReturningMarkersService returningMarkersService;

    @Autowired
    private ReturningMarkersRepository returningMarkersRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    private String usersToken;
    private String adminToken;
    private Users defaultUsers;
    private Users admin;

    @AfterEach
    void teardown() {
        returningMarkersRepository.deleteAll();
        usersRepository.deleteAll();
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
        defaultUsers = Users.builder()
                .UID("testUID")
                .usersId("testUsersId")
                .userRole(UserRole.ROLE_USER)
                .name("testUser")
                .build();

        admin = Users.builder()
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
    @DisplayName("[Integration] 반납소 저장")
    void Integration_관리자계정으로_반납소저장() throws Exception {
        //given
        ReturningMarkersSaveRequestDto requestDto = ReturningMarkersSaveRequestDto.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .imageUrl("testImageUrl.com")
                .build();

        ReturningMarkers returningMarkers = requestDto.toEntity();
        ReturningMarkersResponseDto responseDto = new ReturningMarkersResponseDto(returningMarkers);

        //when
        mockMvc.perform(post("/api/v3/markers/returning/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .header("token", adminToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)))
                .andDo(document("ReturningMarkers-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_ADMIN")
                        ),
                        requestFields(
                                fieldWithPath("latitude").description("Value of latitude (float)").type(JsonFieldType.NUMBER),
                                fieldWithPath("longitude").description("Value of longitude (float)").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Name of renting place").type(JsonFieldType.STRING),
                                fieldWithPath("imageUrl").description("Image Url for rentingMarkers").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("latitude").description("Value of latitude (float)").type(JsonFieldType.NUMBER),
                                fieldWithPath("longitude").description("Value of longitude (float)").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Name of renting place").type(JsonFieldType.STRING),
                                fieldWithPath("imageUrl").description("Image Url for rentingMarkers").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("[Integration] 반납소 목록 전체 조회")
    void Integration_반납소목록_전체조회() throws Exception {
        ReturningMarkersSaveRequestDto requestDto1 = ReturningMarkersSaveRequestDto.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .imageUrl("testImageUrl.com")
                .build();
        ReturningMarkers returningMarkers1 = requestDto1.toEntity();
        ReturningMarkersResponseDto responseDto1 = new ReturningMarkersResponseDto(returningMarkers1);

        ReturningMarkersSaveRequestDto requestDto2 = ReturningMarkersSaveRequestDto.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .imageUrl("testImageUrl.com")
                .build();
        ReturningMarkers returningMarkers2 = requestDto1.toEntity();
        ReturningMarkersResponseDto responseDto2 = new ReturningMarkersResponseDto(returningMarkers2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v3/markers/returning/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto1))
                        .header("token", adminToken)
                )
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v3/markers/returning/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto2))
                        .header("token", adminToken)
                )
                .andExpect(status().isOk());

        List<ReturningMarkersResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(responseDto1);
        responseDtoList.add(responseDto2);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/markers/renting/findAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", usersToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDtoList)))
                .andDo(document("ReturningMarkers-findAll",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_USERS")
                        ),
                        responseFields(
                                fieldWithPath("[].latitude").description("Value of latitude (float)").type(JsonFieldType.NUMBER),
                                fieldWithPath("[].longitude").description("Value of longitude (float)").type(JsonFieldType.NUMBER),
                                fieldWithPath("[].name").description("Name of renting place").type(JsonFieldType.STRING),
                                fieldWithPath("[].imageUrl").description("Image Url for rentingMarkers").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();
    }
}
