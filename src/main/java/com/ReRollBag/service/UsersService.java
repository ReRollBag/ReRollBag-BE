package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Tokens.AccessTokenResponseDto;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.exceptions.usersExceptions.DuplicateUserSaveException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.repository.UsersRepository;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
    public UsersLoginResponseDto save(UsersSaveRequestDto requestDto) throws UsersIdOrPasswordInvalidException, FirebaseAuthException {
        // 이미 저장되어 있는 회원인 경우, 바로 예외 처리
        if (usersRepository.existsByUsersId(requestDto.getUsersId())) throw new DuplicateUserSaveException();

        // idToken 을 활용해서 UID 를 조회. 만약 idToken 이 올바르지 않다면 FirebaseAuthException Throw
        String UID = getUID(requestDto.getIdToken());

        // idToken 검증 및 UID 조회 성공 시, Users 객체 생성
        Users users = requestDto.toEntity();

        // 생성된 Users 객체의 PK 를 조회한 UID 로 Set
        users.setUID(UID);

        // 주어진 정보 바탕으로 users 저장
        usersRepository.save(users);

        // save 이후 login 까지 한 번에 처리
        String accessToken = jwtTokenProvider.createAccessToken(UID);
        String refreshToken = jwtTokenProvider.createRefreshToken(UID);

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

    public UsersLoginResponseDto login(String idToken) throws UsersIdOrPasswordInvalidException, FirebaseAuthException {
        // Verifying idToken and get UID from idToken
        String targetUID = getUID(idToken);

        String accessToken = jwtTokenProvider.createAccessToken(targetUID);
        String refreshToken = jwtTokenProvider.createRefreshToken(targetUID);

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

    public String getUID(String idToken) throws FirebaseAuthException {
        return "Hello12345";
//        return FirebaseAuth.getInstance().verifyIdToken(idToken).getUid();
    }

}
