package com.ReRollBag.integration;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Markers.MarkersResponseDto;
import com.ReRollBag.domain.dto.Markers.MarkersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.repository.MarkersRepository;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.MarkersService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class MarkersIntegrationTest {

    @Autowired
    private MarkersService markersService;

    @Autowired
    private MarkersRepository markersRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String usersToken;
    private String adminToken;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void teardown() {
        markersRepository.deleteAll();
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
    @DisplayName("[Integration] 마커 저장")
    void Integration_마커저장() throws Exception {
        //given
        MarkersSaveRequestDto markersSaveRequestDto = MarkersSaveRequestDto.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("testMarker")
                .markerType("Rent")
                .build();

        MarkersResponseDto markersResponseDto = new MarkersResponseDto(
                1L,
                "testMarker",
                12345.54321,
                54321.12345,
                "Rent"
        );

        //when
        mockMvc.perform(post("/api/v3/markers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(markersSaveRequestDto))
                        .header("token", adminToken)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(markersResponseDto)))
                .andDo(document("Markers-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_ADMIN")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of marker").type(JsonFieldType.STRING),
                                fieldWithPath("latitude").description("Latitude value").type(JsonFieldType.NUMBER),
                                fieldWithPath("longitude").description("Longitude value").type(JsonFieldType.NUMBER),
                                fieldWithPath("markerType").description("markerType : Rent or Return").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("markersId").description("Id of marker").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Name of marker").type(JsonFieldType.STRING),
                                fieldWithPath("latitude").description("Latitude value").type(JsonFieldType.NUMBER),
                                fieldWithPath("longitude").description("Longitude value").type(JsonFieldType.NUMBER),
                                fieldWithPath("markerType").description("markerType : Rent or Return").type(JsonFieldType.STRING)
                        )
                ));

        //then
    }
}
