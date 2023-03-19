package com.ReRollBag.service;

import com.ReRollBag.domain.dto.ReturningMarkers.ReturningMarkersResponseDto;
import com.ReRollBag.domain.dto.ReturningMarkers.ReturningMarkersSaveRequestDto;
import com.ReRollBag.domain.entity.ReturningMarkers;
import com.ReRollBag.repository.ReturningMarkersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class ReturningMarkersService {
    private final ReturningMarkersRepository returningMarkersRepository;

    @Transactional
    public ReturningMarkersResponseDto save(ReturningMarkersSaveRequestDto requestDto) {
        ReturningMarkers markers = requestDto.toEntity();
        returningMarkersRepository.save(markers);
        return new ReturningMarkersResponseDto(markers);
    }

    public List<ReturningMarkersResponseDto> findAll() {
        List<ReturningMarkers> markersList = returningMarkersRepository.findAll();
        List<ReturningMarkersResponseDto> responseDtoList = new ArrayList<>();

        for (ReturningMarkers markers : markersList)
            responseDtoList.add(new ReturningMarkersResponseDto(markers));

        return responseDtoList;
    }
}
