package com.ReRollBag.integration;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Admin.VerifyAdminRequestCertificationNumberDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.repository.AccessTokenRepository;
import com.ReRollBag.repository.CertificationNumberRepository;
import com.ReRollBag.repository.RefreshTokenRepository;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.AdminService;
import com.ReRollBag.service.UsersService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class AdminIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AdminService adminService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CertificationNumberRepository certificationNumberRepository;
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private MockMvc mockMvc;
    private String accessTokenForUsers;
    private String refreshTokenForUsers;
    private String accessTokenForAdmin;
    private String refreshTokenForAdmin;
    private Users defaultUsers;
    private Users defaultAdmin;

    @BeforeEach
    void setUpForSpringRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void init_saveTestUsersAndAdminUsers() throws Exception {
        defaultUsers = Users.builder()
                .UID("testUID")
                .usersId("testUsersId")
                .userRole(UserRole.ROLE_USER)
                .name("testUser")
                .build();
        usersRepository.save(defaultUsers);

        defaultAdmin = Users.builder()
                .UID("testAdminUID")
                .usersId("testAdminId")
                .userRole(UserRole.ROLE_ADMIN)
                .name("testAdmin")
                .build();
        usersRepository.save(defaultAdmin);
    }

    @BeforeEach
    void init_jwtToken() throws Exception {
        accessTokenForUsers = jwtTokenProvider.createAccessToken(defaultUsers.getUID(), defaultUsers.getUsersId());
        refreshTokenForUsers = jwtTokenProvider.createRefreshToken(defaultUsers.getUID(), defaultUsers.getUsersId());
        accessTokenForAdmin = jwtTokenProvider.createAccessToken(defaultAdmin.getUID(), defaultUsers.getUsersId());
        refreshTokenForAdmin = jwtTokenProvider.createRefreshToken(defaultAdmin.getUID(), defaultUsers.getUsersId());
    }

    @AfterEach
    void tearDown() {
        usersRepository.deleteAll();
        certificationNumberRepository.deleteAll();
        accessTokenRepository.deleteAll();
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("[Integration] 관리자 신청")
    void Integration_일반계정으로_관리자신청() throws Exception {
        String expectedMessages = "Successfully request Admin";

        mockMvc.perform(post("/api/v1/users/requestAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessTokenForUsers)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessages))
                .andDo(print());

    }

    @Test
    @DisplayName("[Integration] 관리자로 관리자 신청")
    void Integration_관리자계정으로_관리자신청() throws Exception {
        String errorMessages = "Users is Already Admin";
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(6000)
                .message(errorMessages)
                .build();

        mockMvc.perform(post("/api/v1/users/requestAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessTokenForUsers)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] 잘못된 인증번호로 관리자 신청 승인 : CertificationSignatureException")
    void Integration_관리자신청후_인증번호입력으로_관리자신청승인_CertificationSignatureException() throws Exception {

        mockMvc.perform(post("/api/v1/users/requestAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessTokenForUsers)
                )
                .andExpect(status().isOk());

        VerifyAdminRequestCertificationNumberDto verifyAdminRequestCertificationNumberDto = VerifyAdminRequestCertificationNumberDto.builder()
                .certificationNumber(1234)
                .region("KOR_SUWON")
                .build();

        String errorMessages = "Certification Number is wrong";
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(6002)
                .message(errorMessages)
                .build();

        mockMvc.perform(post("/api/v1/users/verifyAdminRequestCertificationNumber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessTokenForUsers)
                        .content(objectMapper.writeValueAsString(verifyAdminRequestCertificationNumberDto))
                )
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print());
    }

    @Test
    @DisplayName("[Integration] 잘못된 인증번호로 관리자 신청 승인 : CertificationTimeExpireException")
    void Integration_관리자신청후_인증번호입력으로_관리자신청승인_CertificationTimeExpireException() throws Exception {

        adminService.setCertificationNumberExpiration(1L);

        mockMvc.perform(post("/api/v1/users/requestAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessTokenForUsers)
                )
                .andExpect(status().isOk());

        VerifyAdminRequestCertificationNumberDto verifyAdminRequestCertificationNumberDto = VerifyAdminRequestCertificationNumberDto.builder()
                .certificationNumber(1234)
                .region("KOR_SUWON")
                .build();

        String errorMessages = "Certification Number is wrong";
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(6002)
                .message(errorMessages)
                .build();

        mockMvc.perform(post("/api/v1/users/verifyAdminRequestCertificationNumber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessTokenForUsers)
                        .content(objectMapper.writeValueAsString(verifyAdminRequestCertificationNumberDto))
                )
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(errorJson)))
                .andDo(print());

        resetCertificationNumberExpiration();
    }

    private void resetCertificationNumberExpiration() {
        adminService.setCertificationNumberExpiration(60 * 5L);
    }

}
