package com.ReRollBag.integration;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Notices.NoticesResponseDto;
import com.ReRollBag.domain.dto.Notices.NoticesSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.repository.NoticesRepository;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.NoticesService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class NoticesIntegrationTest {

    @Autowired
    private NoticesService noticesService;

    @Autowired
    private NoticesRepository noticesRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MockMvc mockMvc;

    private String usersToken;
    private String adminToken;

    @AfterEach
    void teardown() {
        noticesRepository.deleteAll();
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
    @DisplayName("[Integration] 공지 저장")
    void Integration_관리자계정으로_공지저장() throws Exception {
        //given
        NoticesSaveRequestDto requestDto = NoticesSaveRequestDto.builder()
                .title("testTitle")
                .content("testContent")
                .build();

        NoticesResponseDto responseDto = NoticesResponseDto.builder()
                .title("testTitle")
                .content("testContent")
                .build();

        //when
        mockMvc.perform(post("/api/v3/notices/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .header("token", adminToken)
                )
                //then
                .andExpect(status().isOk())
                .andDo(document("Bags-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("Token").description("AccessToken Value for ROLE_ADMIN")
                        ),
                        requestFields(
                                fieldWithPath("title").description("Value of Title").type(JsonFieldType.STRING),
                                fieldWithPath("content").description("Value of Content").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("title").description("Value of Title").type(JsonFieldType.STRING),
                                fieldWithPath("content").description("Value of Content").type(JsonFieldType.STRING),
                                fieldWithPath("createdAt").description("LocalDateTime when is created").type(JsonFieldType.STRING),
                                fieldWithPath("updatedAt").description("LocalDateTime when is updated").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();
    }
}
