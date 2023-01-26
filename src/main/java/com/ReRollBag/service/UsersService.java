package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Tokens.AccessTokenResponseDto;
import com.ReRollBag.domain.dto.Users.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.exceptions.usersExceptions.DuplicateUserSaveException;
import com.ReRollBag.exceptions.usersExceptions.NicknameAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UsersLoginResponseDto save(UsersSaveRequestDto requestDto) throws UsersIdOrPasswordInvalidException {
        // 이미 저장되어 있는 회원인 경우, 바로 예외 처리
        if (usersRepository.existsByUsersId(requestDto.getUsersId())) throw new DuplicateUserSaveException();

        // 주어진 정보 바탕으로 users 저장
        Users users = requestDto.toEntity();
        String encryptedPassword = passwordEncoder.encode(users.getPassword());
        users.setPassword(encryptedPassword);
        usersRepository.save(users);

        // save 이후 login 까지 한 번에 처리
        UsersLoginRequestDto usersLoginRequestDto = new UsersLoginRequestDto(users.getUsersId(), users.getPassword());
        String accessToken = jwtTokenProvider.createAccessToken(requestDto.getUsersId());
        String refreshToken = jwtTokenProvider.createRefreshToken(requestDto.getUsersId());

        return UsersLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public MockResponseDto checkUserExist(String usersId) throws UsersIdAlreadyExistException {
        boolean result = usersRepository.existsByUsersId(usersId);
        return MockResponseDto.builder()
                .data(!result)
                .build();
    }

    public MockResponseDto checkNicknameExist(String nickname) throws NicknameAlreadyExistException {
        boolean result = usersRepository.existsByNickname(nickname);
        return MockResponseDto.builder()
                .data(!result)
                .build();
    }

    public UsersLoginResponseDto login(UsersLoginRequestDto requestDto) throws UsersIdOrPasswordInvalidException {
        String targetUsersId = requestDto.getUsersId();
        Users targetUsers = usersRepository.findByUsersId(targetUsersId);

        if (targetUsers == null)
            throw new UsersIdOrPasswordInvalidException();

        if (!passwordEncoder.matches(requestDto.getPassword(), targetUsers.getPassword()))
            throw new UsersIdOrPasswordInvalidException();

        String accessToken = jwtTokenProvider.createAccessToken(requestDto.getUsersId());
        String refreshToken = jwtTokenProvider.createRefreshToken(requestDto.getUsersId());

        return UsersLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AccessTokenResponseDto reIssue(HttpServletRequest request) {
        return jwtTokenProvider.reIssue(request);
    }

    public boolean dummyMethod() {
        log.info("dummyMethod is called! Success to Authentication");
        return true;
    }
}
