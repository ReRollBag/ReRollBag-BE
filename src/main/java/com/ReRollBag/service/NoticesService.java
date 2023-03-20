package com.ReRollBag.service;

import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.dto.Notices.NoticesResponseDto;
import com.ReRollBag.domain.dto.Notices.NoticesSaveRequestDto;
import com.ReRollBag.domain.entity.Notices;
import com.ReRollBag.repository.NoticesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class NoticesService {
    private static MockResponseDto responseDto = new MockResponseDto(true);
    private final NoticesRepository noticesRepository;

    @Transactional
    public NoticesResponseDto save(NoticesSaveRequestDto requestDto) {
        Notices notices = requestDto.toEntity();
        noticesRepository.save(notices);
        return new NoticesResponseDto(notices);
    }

    public NoticesResponseDto getLastNotices() {
        Notices lastNotices = noticesRepository.findTopByUpdatedAtOrderByUpdatedAt();
        return new NoticesResponseDto(lastNotices);
    }
}
