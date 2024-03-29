package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Bags.BagsRentingHistoryDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Tokens.AccessTokenResponseDto;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.Users.UsersResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.domain.entity.UsersBagsRentingHistory;
import com.ReRollBag.enums.BagsListType;
import com.ReRollBag.exceptions.usersExceptions.DuplicateUserSaveException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.repository.UsersBagsRentingHistoryRepository;
import com.ReRollBag.repository.UsersRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersBagsRentingHistoryRepository usersBagsRentingHistoryRepository;

    @Transactional
    public UsersLoginResponseDto save(UsersSaveRequestDto requestDto) throws UsersIdOrPasswordInvalidException, FirebaseAuthException {
        // 이미 저장되어 있는 회원인 경우, 바로 예외 처리
        if (usersRepository.existsByUsersId(requestDto.getUsersId())) throw new DuplicateUserSaveException();

        // idToken 을 활용해서 UID 를 조회. 만약 idToken 이 올바르지 않다면 FirebaseAuthException Throw
        String UID = getUIDFromIdToken(requestDto.getIdToken());

        // RequestDto 안에 있는 usersId 를 조회.
        String usersId = requestDto.getUsersId();

        // idToken 검증 및 UID 조회 성공 시, Users 객체 생성
        Users users = requestDto.toEntity();

        // 생성된 Users 객체의 PK 를 조회한 UID 로 Set
        users.setUID(UID);

        // 주어진 정보 바탕으로 users 저장
        usersRepository.save(users);

        // save 이후 login 까지 한 번에 처리
        String accessToken = jwtTokenProvider.createAccessToken(UID, usersId);
        String refreshToken = jwtTokenProvider.createRefreshToken(UID, usersId);

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
        String targetUID = getUIDFromIdToken(idToken);

        // Make new users
        Users users = new Users();

        // Get usersId from UsersRepository
        try {
            users = usersRepository.findById(targetUID).orElseThrow(
                    () -> new IllegalArgumentException()
            );
        } catch (IllegalArgumentException e) {
            // If there is no information with UsersRepository,
            // Make new requestDto with IdToken
            // and return to save method
            UsersSaveRequestDto requestDto = getUserDataFromIdToken(idToken);
            return save(requestDto);
        }

        String targetUsersId = users.getUsersId();

        return createToken(targetUID, targetUsersId);
    }

    public UsersLoginResponseDto createToken(String UID, String usersId) {
        String accessToken = jwtTokenProvider.createAccessToken(UID, usersId);
        String refreshToken = jwtTokenProvider.createRefreshToken(UID, usersId);

        return UsersLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

//    private String getNameFromIdToken(String idToken) throws FirebaseAuthException {
//        return FirebaseAuth.getInstance().getUser(idToken).getDisplayName();
//    }
//
//    private String getEmailFromIdToken(String idToken) throws FirebaseAuthException {
//        return FirebaseAuth.getInstance().getUser(idToken).getEmail();
//    }

    public AccessTokenResponseDto reIssue(HttpServletRequest request) {
        return jwtTokenProvider.reIssue(request);
    }

    public boolean dummyMethod() {
        log.info("dummyMethod is called! Success to Authentication");
        return true;
    }

    public String getUIDFromIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken).getUid();
    }

    private UsersSaveRequestDto getUserDataFromIdToken(String idToken) throws FirebaseAuthException {
        FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(idToken);
        log.info("getUserDataFromIdToken, email : " + token.getEmail());
        log.info("getUserDataFromIdToken, name : " + token.getName());
        log.info("getUserDataFromIdToken, idToken : " + idToken);

        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                token.getEmail(),
                token.getName(),
                idToken
        );

        log.info("getUserDataFromIdToken, email : " + requestDto.getUsersId());
        log.info("getUserDataFromIdToken, name : " + requestDto.getName());
        log.info("getUserDataFromIdToken, idToken : " + requestDto.getIdToken());
        return requestDto;
    }


    public boolean deleteDummy(String usersId) {
        Users users = usersRepository.findByUsersId(usersId);
        usersRepository.deleteById(users.getUID());
        return true;
    }

    public List<BagsResponseDto> getBagsList(String token, BagsListType type) {
        // get UID and Users Entity from Token
        String UID = jwtTokenProvider.getUID(token);
        String usersId = jwtTokenProvider.getUsersId(token);
        Users users = usersRepository.findByUsersId(usersId);

        // get Each Bags List by BagsListType parameter
        List<Bags> bagsListInUsersEntity;
        switch (type) {
            case RentingBagsList:
                bagsListInUsersEntity = users.getRentingBagsList();
                break;
            case ReturningBagsList:
                bagsListInUsersEntity = users.getReturningBagsList();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        // Make new BagsResponseDto ArrayList
        List<BagsResponseDto> responseDtoList = new ArrayList<>();

        // Make each dto with bags in bagsListInUsersEntity
        for (Bags bags : bagsListInUsersEntity) {
            BagsResponseDto responseDto = new BagsResponseDto(bags);
            responseDtoList.add(responseDto);
        }

        // Sort responseDtoList with time
        Collections.sort(responseDtoList);

        return responseDtoList;
    }

    public List<BagsRentingHistoryDto> getReturnedHistoryList(String token) {
        // get UID from Token
        String UID = jwtTokenProvider.getUID(token);

        UsersBagsRentingHistory history = usersBagsRentingHistoryRepository.findById(UID).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );

        log.info("!!getReturnedHistoryList!!");
        log.info(history.getUsersBagsRentingHistory().size());
        return history.getUsersBagsRentingHistory();
    }

    public UsersResponseDto getUsersInfo(String token) {
        String usersId = jwtTokenProvider.getUsersId(token);
        Users targetUsers = usersRepository.findByUsersId(usersId);
        return new UsersResponseDto(targetUsers);
    }

}
