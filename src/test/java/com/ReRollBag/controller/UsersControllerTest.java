package com.ReRollBag.controller;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.exceptions.ErrorCode;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.usersExceptions.DuplicateUserSaveException;
import com.ReRollBag.exceptions.usersExceptions.NicknameAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(UsersController.class)
//@AutoConfigureMockMvc in @WebMvcTest
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

    @Test
    @DisplayName("[Controller] 회원 가입")
    void Controller_회원가입_테스트() throws Exception {
        //given
        Users users = Users.builder()
                .usersId("test@gmail.com")
                .nickname("testNickname")
                .password("testPassword")
                .build();

        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
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
                "testPassword"
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
        UsersLoginRequestDto requestDto = new UsersLoginRequestDto("test@gmail.com", "testPassword");
        UsersLoginResponseDto responseDto = UsersLoginResponseDto.builder()
                .accessToken("testAccessToken")
                .refreshToken("testRefreshToken")
                .build();
        //mocking
        when(usersService.login(any())).thenReturn(responseDto);
        //when
        mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)))
                .andDo(print());
    }

    @Test
    @DisplayName("[Controller] 잘못된 ID 또는 PW로 로그인 시도 예외")
    void Controller_잘못된IDPW_로그인_예외_테스트() throws Exception {
        //given
        UsersLoginRequestDto requestDto = new UsersLoginRequestDto("test@gmail.com", "testPassword");

        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(ErrorCode.UsersIdOrPasswordInvalidException.getErrorCode())
                .message("UsersIdOrPasswordInvalidException")
                .build();

        //mocking
        when(usersService.login(any())).thenThrow(UsersIdOrPasswordInvalidException.class);

        //when
        mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
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
        //mocking
        when(usersService.checkUserExist(usersId)).thenReturn(true);
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

    @Test
    @DisplayName("[Controller] 닉네임 중복 검사 성공 case")
    void Controller_닉네임_중복검사_성공() throws NicknameAlreadyExistException {
        //given
        String nickname = "nickname";
        //mocking
        when(usersService.checkNicknameExist(nickname)).thenReturn(true);
        //when
        try {
            mockMvc.perform(get("/api/v2/users/checkNicknameExist/" + nickname))
                    //then
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (NicknameAlreadyExistException e) {
            throw new NicknameAlreadyExistException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Controller] 닉네임 중복 검사 실패 case")
    void Controller_닉네임_중복검사_실패() throws NicknameAlreadyExistException {
        //given
        String nickname = "nickname";
        //mocking
        when(usersService.checkNicknameExist(nickname)).thenThrow(NicknameAlreadyExistException.class);
        //when
        try {
            mockMvc.perform(get("/api/v2/users/checkNicknameExist/" + nickname))
                    //then
                    .andExpect(status().isAccepted())
                    .andReturn();
        } catch (NicknameAlreadyExistException e) {
            throw new NicknameAlreadyExistException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
