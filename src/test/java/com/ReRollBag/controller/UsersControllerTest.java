package com.ReRollBag.controller;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.UsersResponseDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc
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

        UsersResponseDto responseDto = UsersResponseDto.builder()
                .users(users)
                .build();

        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );

        //mocking
        when(usersService.save(any())).thenReturn(responseDto);


        //when
        mockMvc.perform(post("/api/v2/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto))
                )
        //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)))
                .andDo(print());

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
    @DisplayName("[Controller] 아이디 중복 검사 성공 case")
    void Controller_아이디_중복검사_성공() throws UsersIdAlreadyExistException {
        //given
        String usersId = "test@gmail.com";
        //mocking
        when(usersService.checkUserExist(usersId)).thenReturn(true);
        //when
        try {
            mockMvc.perform(get("/api/v2/users/checkUserExist/"+usersId))
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
