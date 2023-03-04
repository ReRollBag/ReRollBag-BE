package com.ReRollBag.service;

import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BagsServiceTest {

    @InjectMocks
    private BagsService bagsService;

    @Mock
    private BagsRepository bagsRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private BagsCount bagsCount;

    @Test
    @DisplayName("[Service] Bags 생성 테스트")
    @Rollback(value = false)
    public void Service_가방생성_테스트() throws Exception {
        //given
        BagsSaveRequestDto bagsSaveRequestDto = new BagsSaveRequestDto(
                "KOR",
                "SUWON"
        );
        Bags bags = bagsSaveRequestDto.toEntity();

        given(bagsRepository.save(any())).willReturn(bags);
        given(bagsCount.getLastIndexWithRegion(any())).willReturn(1L);
        given(bagsCount.isExistWithRegion(any())).willReturn(false);

        //when
        BagsResponseDto result = bagsService.save(bagsSaveRequestDto);

        //then
        assertThat(result.getBagsId()).isEqualTo("KOR_SUWON_1");
        assertThat(result.isRented()).isEqualTo(false);
        assertThat(result.getWhenIsRented()).isEqualTo(LocalDateTime.MIN.toString());
        assertThat(result.getRentingUsersId()).isEqualTo("");
    }

    @Test
    @DisplayName("[Service] Bags Rent 테스트")
    public void Service_가방렌트_테스트() {
        //given
        MockResponseDto responseDto = new MockResponseDto(true);

        BagsSaveRequestDto bagsSaveRequestDto = new BagsSaveRequestDto(
                "KOR",
                "SUWON"
        );
        Bags bags = bagsSaveRequestDto.toEntity();
        assert bags != null;

        String usersId = "test@gmail.com";
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testIdToken",
                UserRole.ROLE_USER.toString()
        );
        Users users = requestDto.toEntity();

        BagsRentOrReturnRequestDto rentOrReturnRequestDto = new BagsRentOrReturnRequestDto(
                users.getUsersId(),
                bags.getBagsId()
        );

        when(bagsRepository.findById(any())).thenReturn(Optional.of(bags));
        when(usersRepository.findByUsersId(any())).thenReturn(users);

        //when
        bagsService.rentOrReturn(rentOrReturnRequestDto);

        //then
        assertThat(bags.isRented()).isEqualTo(true);
        assertThat(bags.getWhenIsRented()).isAfter(LocalDateTime.MIN);
        assertThat(bags.getRentingUsers().getUsersId()).isEqualTo(usersId);
        assertThat(users.getRentingBagsList().get(0)).isEqualTo(bags);
    }

    @Test
    @DisplayName("[Service] Bags Return 테스트")
    public void Service_가방반납_테스트() {
        //given
        MockResponseDto responseDto = new MockResponseDto(true);

        String usersId = "test@gmail.com";
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testIdToken",
                UserRole.ROLE_USER.toString()
        );
        Users users = requestDto.toEntity();

        Bags bags = Bags.builder()
                .bagsId("KOR_SUWON_1")
                .whenIsRented(LocalDateTime.now())
                .isRented(true)
                .rentingUsers(users)
                .build();

        users.getRentingBagsList().add(bags);

        BagsRentOrReturnRequestDto rentOrReturnRequestDto = new BagsRentOrReturnRequestDto(
                users.getUsersId(),
                bags.getBagsId()
        );

        when(bagsRepository.findById(any())).thenReturn(Optional.of(bags));
        when(usersRepository.findByUsersId(any())).thenReturn(users);

        bagsService.rentOrReturn(rentOrReturnRequestDto);

        assertThat(bags.isRented()).isEqualTo(false);
        assertThat(bags.getWhenIsRented()).isEqualTo(LocalDateTime.MIN);
        assertThat(bags.getRentingUsers()).isNull();
        assertThat(users.getReturningBagsList().size()).isEqualTo(0);
        assertThat(users.getReturnedBagsList().get(0)).isEqualTo(bags);
    }
}
