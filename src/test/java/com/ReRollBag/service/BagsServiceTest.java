package com.ReRollBag.service;

import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.repository.BagsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

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
    private BagsCount bagsCount;

    @Test
    @DisplayName("[Service] Bags 생성")
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
        assertThat(result.getWhenIsRented()).isNull();
        assertThat(result.getRentingUsersId()).isNull();
    }
}
