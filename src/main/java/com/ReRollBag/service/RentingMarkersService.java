package com.ReRollBag.service;

import com.ReRollBag.domain.dto.RentingMarkers.RentingMarkersResponseDto;
import com.ReRollBag.domain.dto.RentingMarkers.RentingMarkersSaveRequestDto;
import com.ReRollBag.domain.entity.RentingMarkers;
import com.ReRollBag.repository.RentingMarkersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class RentingMarkersService {
    private final RentingMarkersRepository rentingMarkersRepository;

    public RentingMarkersResponseDto save(RentingMarkersSaveRequestDto requestDto) {
        RentingMarkers rentingMarkers = requestDto.toEntity();
        rentingMarkersRepository.save(rentingMarkers);
        return new RentingMarkersResponseDto(rentingMarkers);
    }
    
}
