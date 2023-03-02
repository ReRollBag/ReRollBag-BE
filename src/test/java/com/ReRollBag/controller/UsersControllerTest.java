package com.ReRollBag.controller;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.ErrorCode;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.usersExceptions.DuplicateUserSaveException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.service.CustomUserDetailService;
import com.ReRollBag.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UsersController.class)
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersController usersController;

    @MockBean
    private UsersService usersService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Test
    @DisplayName("[Controller] 회원 가입")
    void Controller_회원가입_테스트() throws Exception {
        //given
        Users users = Users.builder()
                .UID("testUID")
                .usersId("test@gmail.com")
                .name("testUsername")
                .userRole(UserRole.ROLE_USER)
                .build();

        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testIdToken",
                null
        );

        UsersLoginResponseDto loginResponseDto = UsersLoginResponseDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        //mocking
        when(usersService.save(any())).thenReturn(loginResponseDto);


        //when
        mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(loginResponseDto)))
                .andDo(print());

    }

    @Test
    @DisplayName("[Controller] 회원 중복 가입 예외")
    void Controller_회원중복가입_예외_테스트() throws Exception {
        //given
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword",
                null
        );

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(ErrorCode.DuplicateUserSaveException.getErrorCode())
                .message("DuplicateUserSaveException")
                .build();

        //mocking
        when(usersService.save(any())).thenThrow(DuplicateUserSaveException.class);

        //when
        mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                //then
                .andExpect(status().isForbidden())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)));
    }

    @Test
    @DisplayName("[Controller] 로그인 및 토큰 발급")
    void Controller_로그인_토큰발급_테스트() throws Exception {
        //given
        String idToken = "idToken";
        UsersLoginResponseDto responseDto = UsersLoginResponseDto.builder()
                .accessToken("testAccessToken")
                .refreshToken("testRefreshToken")
                .build();


        when(usersService.login(any())).thenReturn(responseDto);

        //then
        mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", idToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)))
                .andDo(print());
    }

    @Test
    @DisplayName("[Controller] 잘못된 idToken으로 로그인 시도 시 예외")
    void Controller_잘못된idToken_로그인_예외_테스트() throws Exception {
        //given
        String invalidIdToken = "invalidIdToken";

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(ErrorCode.FirebaseAuthException.getErrorCode())
                .message("FirebaseAuthException")
                .build();

        //mocking
        when(usersService.login(any())).thenThrow(FirebaseAuthException.class);

        //when
        mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Token", invalidIdToken)
                )
                //then
                .andExpect(status().isForbidden())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(errorJson)));
    }

    @Test
    @DisplayName("[Controller] 아이디 중복 검사 성공 case")
    void Controller_아이디_중복검사_성공() throws UsersIdAlreadyExistException {
        //given
        String usersId = "test@gmail.com";
        MockResponseDto responseDto = MockResponseDto.builder()
                .data(true)
                .build();
        //mocking
        when(usersService.checkUserExist(usersId)).thenReturn(responseDto);
        //when
        try {
            mockMvc.perform(get("/api/v2/users/checkUserExist/" + usersId))
                    //then
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (UsersIdAlreadyExistException e) {
            throw new UsersIdAlreadyExistException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Controller] 아이디 중복 검사 실패 case")
    void Controller_아이디_중복검사_실패() throws UsersIdAlreadyExistException {
        //given
        String usersId = "test@gmail.com";
        //mocking
        when(usersService.checkUserExist(usersId)).thenThrow(UsersIdAlreadyExistException.class);
        //when
        try {
            mockMvc.perform(get("/api/v2/users/checkUserExist/" + usersId))
                    //then
                    .andExpect(status().isAccepted())
                    .andReturn();
        } catch (UsersIdAlreadyExistException e) {
            throw new UsersIdAlreadyExistException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Controller] Token 없이 v1 메소드 테스트")
    void Controller_토큰검증_실패() throws Exception {
        mockMvc.perform(get("/api/v1/users/dummyMethod"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

}
