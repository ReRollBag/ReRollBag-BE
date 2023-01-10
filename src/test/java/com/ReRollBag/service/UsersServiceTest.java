package com.ReRollBag.service;

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
}
