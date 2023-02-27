package com.ReRollBag.integration;

import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.service.BagsService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(RestDocumentationExtension.class)
public class BagsIntegrationTest {

    @Autowired
    private BagsService bagsService;

    @Autowired
    private BagsRepository bagsRepository;

    @Autowired
    private MockMvc mockMvc;

    private String usersToken;
    private String adminToken;

    @BeforeEach
    void setUpForSpringRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @BeforeEach
    void saveUsersAndAdminAndSetUpTokens() throws Exception {
        UsersSaveRequestDto usersSaveRequestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword",
                null
        );

        UsersSaveRequestDto adminSaveRequestDto = new UsersSaveRequestDto(
                "admin",
                "admin",
                "testPassword",
                "ROLE_ADMIN"
        );

        MvcResult usersSaveResult = mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usersSaveRequestDto))
                )
                .andReturn();

        String content = usersSaveResult.getResponse().getContentAsString();
        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
        usersToken = loginResponseDto.getAccessToken();

        content = usersSaveResult.getResponse().getContentAsString();
        loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
        adminToken = loginResponseDto.getAccessToken();

        String.valueOf(mockMvc.perform(post("/api/v2/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(adminSaveRequestDto))
        ));
    }

    @Test
    @DisplayName("[Integration] 가방 저장")
    void Integration_관리자계정으로_가방저장() throws Exception {
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
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(bagsResponseDto)))
                .andDo(document("Bags-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
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
    @DisplayName("[Integration] 가방 한번 더 저장해서 index 증가 테스트")
    void Integration_관리자계정으로_가방한번더저장_index증가_테스트() throws Exception {
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
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(bagsResponseDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("[Integration] 가방 대여 테스트")
    void Integration_가방대여_테스트() throws Exception {
        //given


        //when


    }

}

