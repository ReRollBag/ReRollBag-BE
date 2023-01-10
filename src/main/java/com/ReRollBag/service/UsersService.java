package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.UsersResponseDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.logging.Logger;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UsersResponseDto save(UsersSaveRequestDto requestDto)  {
        Users users = requestDto.toEntity();
        String encryptedPassword = passwordEncoder.encode(users.getPassword());
        users.setPassword(encryptedPassword);
        usersRepository.save(users);
        return new UsersResponseDto(users);
    }

    public UsersResponseDto findByUsersId (String usersId) {
        Users users = usersRepository.findByUsersId(usersId);
        if (users == null) throw new IllegalArgumentException("Users is not Exists");
        return UsersResponseDto.builder()
                .users(users)
                .build();
    }
    public UsersLoginResponseDto login (UsersLoginRequestDto requestDto) {
        String targetUsersId = requestDto.getUsersId();
        Users targetUsers = usersRepository.findByUsersId(targetUsersId);

        if (targetUsers == null) throw new IllegalArgumentException("Users Id is wrong");

        if (!passwordEncoder.matches(requestDto.getPassword(), targetUsers.getPassword())) {
            throw new IllegalArgumentException("Password is wrong");
        }

        if (jwtTokenProvider == null) log.error("jwtTokenProvider is NULL!");
        String accessToken = jwtTokenProvider.createAccessToken(requestDto.getUsersId());
        String refreshToken = jwtTokenProvider.createRefreshToken(requestDto.getUsersId());

        return UsersLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
