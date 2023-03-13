package com.ReRollBag.service;

import com.ReRollBag.domain.dto.Markers.MarkersResponseDto;
import com.ReRollBag.domain.dto.Markers.MarkersSaveRequestDto;
import com.ReRollBag.domain.entity.Markers;
import com.ReRollBag.enums.MarkerType;
import com.ReRollBag.repository.MarkersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MarkersServiceTest {
    @InjectMocks
    private MarkersService markersService;

    @Mock
    private MarkersRepository markersRepository;

    @Test
    @DisplayName("[Service] Markers 생성 테스트")
    public void Service_마커생성_테스트() throws Exception {
        //given
        MarkersSaveRequestDto requestDto = MarkersSaveRequestDto.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("testMarker")
                .markerType("Rent")
                .build();
        Markers markers = requestDto.toEntity();
        markers.setMarkersId(1L);

        //when
        given(markersRepository.save(any())).willReturn(markers);
        MarkersResponseDto responseDto = markersService.save(requestDto);

        //then
        assertThat(responseDto.getMarkersId()).isEqualTo(1L);
        assertThat(responseDto.getLatitude()).isEqualTo(12345.54321);
        assertThat(responseDto.getLongitude()).isEqualTo(54321.12345);
        assertThat(responseDto.getName()).isEqualTo("testMarker");
        assertThat(responseDto.getMarkerType()).isEqualTo("Rent");
    }
}
