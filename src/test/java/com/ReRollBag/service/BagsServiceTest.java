package com.ReRollBag.service;

import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
                "testPassword",
                null
        );
        Users users = requestDto.toEntity();

        BagsRentOrReturnRequestDto rentOrReturnRequestDto = new BagsRentOrReturnRequestDto(
                users.getUsersId(),
                bags.getBagsId()
        );

        given(bagsRepository.findById(any())).willReturn(Optional.of(bags));
        given(usersRepository.findByUsersId(any())).willReturn(users);

        //when
        bagsService.rentOrReturn(rentOrReturnRequestDto);

        //then
        assertThat(bags.isRented()).isEqualTo(true);
        assertThat(bags.getWhenIsRented()).isAfter(LocalDateTime.MIN);
        assertThat(bags.getRentingUsers().getUsersId()).isEqualTo(usersId);
        assertThat(users.getRentingBagsList().get(0)).isEqualTo(bags);
    }
}
