package com.ReRollBag.service;

import com.ReRollBag.domain.entity.RentingMarkers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RentingMarkersServiceTest {

    @InjectMocks
    private RentingMarkersService rentingMarkersService;

    @Mock
    private RentingMarkersRepository rentingMarkersRepository;

    @Test
    @DisplayName("[Service] RentingMarkers 생성 테스트")
    public void Service_대여소생성_테스트() throws Exception {
        //given
        RentingMarkersSaveRequestDto requestDto = RentingMarkersSaveRequestDto.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .maxBagsNum(5)
                .currentBagsNum(5)
                .build();
        RentingMarkers rentingMarkers = requestDto.toEntity();
        rentingMarkers.setMarkersId(1L);

        //when
        when(rentingMarkersRepository.save(any())).willReturn(rentingMarkers);
        RentingMarkersResponseDto responseDto = rentingMarkersService.save(requestDto);

        //then
        assertThat(responseDto.getMarkersId()).isEqualTo(1L);
        assertThat(responseDto.getLatitude()).isEqualTo(12345.54321);
        assertThat(responseDto.getLongitude()).isEqualTo(54321.12345);
        assertThat(responseDto.getName()).isEqualTo("GS25 우만점");
        assertThat(responseDto.getMaxBagsNum()).isEqualTo(5);
        assertThat(responseDto.getCurrentBagsNum()).isEqualTo(5);
    }

}
