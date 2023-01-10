package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.UsersResponseDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {
    @InjectMocks
    private UsersService usersService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailService userDetailService;


    @Test
    @DisplayName("[Service] 회원 가입")
    public void Service_회원가입_테스트 () {
        //given
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );
        Users users = requestDto.toEntity();
        UsersResponseDto responseDto = new UsersResponseDto(users);

        String expectedUsersId = "test@gmail.com";
        String expectedNickname = "testNickname";

        //mocking
        given(usersRepository.save(any()))
                .willReturn(users);

        //when
        UsersResponseDto targetResponseDto = usersService.save(requestDto);

        //then
        assertThat(targetResponseDto.nickname).isEqualTo(expectedNickname);
        assertThat(targetResponseDto.usersId).isEqualTo(expectedUsersId);
    }

//    @Test
//    @DisplayName("[Service] 로그인 및 토큰 발급")
//    public void Service_로그인_토큰발급_테스트 () {
//        //given
//        UsersLoginRequestDto loginRequestDto = new UsersLoginRequestDto(
//                "test@gmail.com",
//                "testPassword"
//        );
//
//        Users users = Users.builder()
//                .usersId("test@gmail.com")
//                .nickname("testNickname")
//                .password("testPassword")
//                .build();
//
//        String encodedPassword = passwordEncoder.encode("testPassword");
//        users.setPassword(encodedPassword);
//
//        System.out.println(encodedPassword);
//
//        //mocking
//        given(usersRepository.findByUsersId(any()))
//                .willReturn(users);
//
//        //when
//        UsersLoginResponseDto target = usersService.login(loginRequestDto);
//
//        System.out.println(target.getAccessToken());
//        System.out.println(target.getRefreshToken());
//
//        //then
//        assertThat(jwtTokenProvider.validateToken(target.getAccessToken())).isEqualTo(true);
//        assertThat(jwtTokenProvider.validateToken(target.getRefreshToken())).isEqualTo(true);
//    }
}
