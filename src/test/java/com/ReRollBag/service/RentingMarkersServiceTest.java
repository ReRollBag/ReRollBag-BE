package com.ReRollBag.service;

import com.ReRollBag.domain.dto.RentingMarkers.RentingMarkersResponseDto;
import com.ReRollBag.domain.dto.RentingMarkers.RentingMarkersSaveRequestDto;
import com.ReRollBag.domain.entity.RentingMarkers;
import com.ReRollBag.repository.RentingMarkersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentingMarkersServiceTest {

    @InjectMocks
    private RentingMarkersService rentingMarkersService;

    @Mock
    private RentingMarkersRepository rentingMarkersRepository;

    @Test
    @DisplayName("[Service] 대여소 생성 테스트")
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
        when(rentingMarkersRepository.save(any())).thenReturn(rentingMarkers);
        RentingMarkersResponseDto responseDto = rentingMarkersService.save(requestDto);

        //then
        assertThat(responseDto.getLatitude()).isEqualTo(12345.54321);
        assertThat(responseDto.getLongitude()).isEqualTo(54321.12345);
        assertThat(responseDto.getName()).isEqualTo("GS25 우만점");
        assertThat(responseDto.getMaxBagsNum()).isEqualTo(5);
        assertThat(responseDto.getCurrentBagsNum()).isEqualTo(5);
    }

    @Test
    @DisplayName("[Service] 대여소 가방 개수 줄이기 테스트")
    public void Service_대여소_가방개수줄이기_테스트() throws Exception {
        //given
        RentingMarkers rentingMarkers = RentingMarkers.builder()
                .markersId(1L)
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .maxBagsNum(5)
                .currentBagsNum(5)
                .build();

        //when
        when(rentingMarkersRepository.findById(1L)).thenReturn(rentingMarkers);
        rentingMarkersService.decreaseCurrentBagsNum(1L);

        //then
        assertThat(rentingMarkers.getCurrentBagsNum()).isEqualTo(4);
    }

    @Test
    @DisplayName("[Service] 대여소 전체조회 테스트")
    public void Service_대여소_전체조회_테스트() throws Exception {
        //given
        RentingMarkers rentingMarkers1 = RentingMarkers.builder()
                .markersId(1L)
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .maxBagsNum(5)
                .currentBagsNum(5)
                .build();

        RentingMarkers rentingMarkers2 = RentingMarkers.builder()
                .markersId(2L)
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 아주대삼거리점")
                .maxBagsNum(8)
                .currentBagsNum(8)
                .build();

        List<RentingMarkers> rentingMarkersList = new ArrayList<>();
        rentingMarkersList.add(rentingMarkers1);
        rentingMarkersList.add(rentingMarkers2);

        //when
        when(rentingMarkersRepository.findAll()).thenReturn(rentingMarkersList);
        List<RentingMarkersResponseDto> responseDtoList = rentingMarkersService.findAll();

        //then
        assertThat(responseDtoList.size()).isEqualTo(2);
        assertThat(responseDtoList.get(0).getName()).isEqualTo("GS25 우만점");
        assertThat(responseDtoList.get(1).getName()).isEqualTo("GS25 아주대삼거리점");
    }
}
