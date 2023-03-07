package com.ReRollBag.service;

import com.ReRollBag.domain.dto.Markers.MarkersResponseDto;
import com.ReRollBag.domain.dto.Markers.MarkersSaveRequestDto;
import com.ReRollBag.domain.entity.Markers;
import com.ReRollBag.repository.MarkersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class MarkersService {
    private final MarkersRepository markersRepository;

    @Transactional
    public MarkersResponseDto save(MarkersSaveRequestDto requestDto) {
        Markers saveMarkers = requestDto.toEntity();
        Markers savedMarkers = markersRepository.save(saveMarkers);
        return new MarkersResponseDto(savedMarkers);
    }
}
