package com.ReRollBag.service;

import com.ReRollBag.domain.entity.ReturningMarkers;
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
public class ReturningMarkersServiceTest {

    @InjectMocks
    private ReturningMarkersService returningMarkersService;

    @Mock
    private ReturningMarkersRepository returningMarkersRepository;

    @Test
    @DisplayName("[Service] 반납소 생성 테스트")
    public void Service_반납소생성_테스트() throws Exception {
        //given
        ReturningMarkersSaveRequestDto requestDto = ReturningMarkers.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .imageUrl("testImageUrl.com")
                .build();
        ReturningMarkers returningMarkers = requestDto.toEntity();
        returningMarkers.setMarkersId(1L);

        //when
        when(returningMarkersRepository.save(any())).thenReturn(returningMarkers);
        ReturningMarkersResponseDto responseDto = returningMarkersService.save(requestDto);

        //then
        assertThat(responseDto.getLatitude()).isEqualTo(12345.54321);
        assertThat(responseDto.getLongitude()).isEqualTo(54321.12345);
        assertThat(responseDto.getName()).isEqualTo("GS25 우만점");
        assertThat(responseDto.getImageUrl()).isEqualTo("testImageUrl.com");
    }

    @Test
    @DisplayName("[Service] 대여소 전체조회 테스트")
    public void Service_대여소_전체조회_테스트() throws Exception {
        //given
        ReturningMarkers returningMarkers1 = ReturningMarkers.builder()
                .markersId(1L)
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .imageUrl("testImageUrl.com")
                .build();

        ReturningMarkers returningMarkers2 = ReturningMarkers.builder()
                .markersId(2L)
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 아주대삼거리점")
                .imageUrl("testImageUrl.com")
                .build();

        List<ReturningMarkers> returningMarkersList = new ArrayList<>();
        returningMarkersList.add(returningMarkers1);
        returningMarkersList.add(returningMarkers2);

        //when
        when(returningMarkersRepository.findAll()).thenReturn(returningMarkersList);
        List<ReturningMarkersResponseDto> responseDtoList = returningMarkersService.findAll();

        //then
        assertThat(responseDtoList.size()).isEqualTo(2);
        assertThat(responseDtoList.get(0).getName()).isEqualTo("GS25 우만점");
        assertThat(responseDtoList.get(1).getName()).isEqualTo("GS25 아주대삼거리점");
    }
}
