package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.usersExceptions.DuplicateUserSaveException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class UsersServiceTest {
    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailService userDetailService;

//    @Test
//    @DisplayName("[Service] 회원 가입")
//    public void Service_회원가입_테스트() throws UsersIdOrPasswordInvalidException, FirebaseAuthException {
//        //given
//
//        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
//                "testUsersId",
//                "testUsername",
//                "testIdToken"
//        );
//
//        Users users = requestDto.toEntity();
//        UsersResponseDto responseDto = new UsersResponseDto(users);
//
//        String expectedUsersId = "test@gmail.com";
//        String expectedNickname = "testNickname";
//
//        //mocking
//        when(usersRepository.save(any()))
//                .thenReturn(users);
//        when(jwtTokenProvider.createAccessToken(any()))
//                .thenReturn("AccessToken");
//        when(jwtTokenProvider.createRefreshToken(any()))
//                .thenReturn("RefreshToken");
//
//        //when
//        UsersLoginResponseDto targetResponseDto = usersService.save(requestDto);
//
//        //then
//        assertThat(targetResponseDto.getAccessToken()).isEqualTo("AccessToken");
//        assertThat(targetResponseDto.getRefreshToken()).isEqualTo("RefreshToken");
//    }

    @Test
    @DisplayName("[Service] 회원 가입 후 같은 정보로 회원가입 시 실패")
    public void Service_회원가입후_같은정보로_회원가입시_실패() throws UsersIdOrPasswordInvalidException {
        //given
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword",
                null
        );

        when(usersRepository.existsByUsersId(any()))
                .thenReturn(true);

        //then
        assertThrows(DuplicateUserSaveException.class, () -> usersService.save(requestDto));
    }

//    @Test
//    @DisplayName("[Service] 로그인 및 토큰 발급 테스트")
//    public void Service_로그인_토큰발급_테스트() throws UsersIdOrPasswordInvalidException, FirebaseAuthException {
//        //given
//        String idToken = "testIdToken";
//
//        Users users = Users.builder()
//                .UID("testUID")
//                .usersId("test@gmail.com")
//                .name("testUsername")
//                .userRole(UserRole.ROLE_USER)
//                .build();
//        //mocking
//        given(usersRepository.findByUsersId(any()))
//                .willReturn(users);
//        given(jwtTokenProvider.createAccessToken(any()))
//                .willReturn("AccessToken");
//        given(jwtTokenProvider.createRefreshToken(any()))
//                .willReturn("RefreshToken");
//
//        //when
//        UsersLoginResponseDto responseDto = usersService.login(idToken);
//
//        //then
//        assertThat(responseDto.getAccessToken()).isEqualTo("AccessToken");
//        assertThat(responseDto.getRefreshToken()).isEqualTo("RefreshToken");
//    }

    @Test
    @DisplayName("[Service] getRentingBagsList 테스트")
    public void Service_getRentingBagsList_테스트() throws Exception {
        //given
        Users users = Users.builder()
                .UID("testUID")
                .usersId("test@gmail.com")
                .name("testUsername")
                .userRole(UserRole.ROLE_USER)
                .build();

        Bags bags1 = Bags.builder()
                .bagsId("KOR_SUWON_1")
                .isRented(true)
                .whenIsRented(LocalDateTime.MIN)
                .rentingUsers(users)
                .build();

        Bags bags2 = Bags.builder()
                .bagsId("KOR_SUWON_2")
                .isRented(true)
                .whenIsRented(LocalDateTime.now())
                .rentingUsers(users)
                .build();

        users.getRentingBagsList().add(bags1);
        users.getRentingBagsList().add(bags2);

        String usersId = "test@gmail.com";

        //when
        when(usersRepository.findByUsersId(usersId)).thenReturn(users);
        List<BagsResponseDto> responseDtoList = usersService.getRentingBagsList(usersId);

        //then
        assertThat(responseDtoList.get(0).getBagsId()).isEqualTo(bags1.getBagsId());
        assertThat(responseDtoList.get(1).getBagsId()).isEqualTo(bags2.getBagsId());

        System.out.println("first : " + responseDtoList.get(0).getBagsId());
        System.out.println("second : " + responseDtoList.get(1).getBagsId());

    }

}
