package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Users.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.Users.UsersResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.exceptions.usersExceptions.DuplicateUserSaveException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void Service_회원가입_테스트() throws UsersIdOrPasswordInvalidException {
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
        given(jwtTokenProvider.createAccessToken(any()))
                .willReturn("AccessToken");
        given(jwtTokenProvider.createRefreshToken(any()))
                .willReturn("RefreshToken");

        //when
        UsersLoginResponseDto targetResponseDto = usersService.save(requestDto);

        //then
        assertThat(targetResponseDto.getAccessToken()).isEqualTo("AccessToken");
        assertThat(targetResponseDto.getRefreshToken()).isEqualTo("RefreshToken");
    }

    @Test
    @DisplayName("[Service] 회원 가입 후 같은 정보로 회원가입 시 실패")
    public void Service_회원가입후_같은정보로_회원가입시_실패() throws UsersIdOrPasswordInvalidException {
        //given
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );

        //mocking
        given(usersRepository.existsByUsersId(any()))
                .willReturn(true);

        //when
        //then
        assertThrows(DuplicateUserSaveException.class, () -> usersService.save(requestDto));
    }

    @Test
    @DisplayName("[Service] 로그인 및 토큰 발급 테스트")
    public void Service_로그인_토큰발급_테스트() throws UsersIdOrPasswordInvalidException {
        //given
        String rawPassword = "testPassword";
        String encodedPassword = "encodedPassword";

        UsersLoginRequestDto requestDto = new UsersLoginRequestDto("test@gmail.com", rawPassword);

        Users users = Users.builder()
                .usersId("test@gmail.com")
                .nickname("testNickname")
                .password(encodedPassword)
                .build();
        //mocking
        given(passwordEncoder.matches(rawPassword, encodedPassword))
                .willReturn(true);
        given(usersRepository.findByUsersId(any()))
                .willReturn(users);
        given(jwtTokenProvider.createAccessToken(any()))
                .willReturn("AccessToken");
        given(jwtTokenProvider.createRefreshToken(any()))
                .willReturn("RefreshToken");

        //when
        UsersLoginResponseDto responseDto = usersService.login(requestDto);

        //then
        assertThat(responseDto.getAccessToken()).isEqualTo("AccessToken");
        assertThat(responseDto.getRefreshToken()).isEqualTo("RefreshToken");
    }

}
