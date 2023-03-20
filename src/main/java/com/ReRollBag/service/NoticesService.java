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
import java.util.ArrayList;
import java.util.List;

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
        Notices lastNotices = noticesRepository.findAll().get(noticesRepository.findAll().size() - 1);
        return new NoticesResponseDto(lastNotices);
    }

    public List<NoticesResponseDto> getAllNotices() {
        List<Notices> noticesList = noticesRepository.findAll();
        List<NoticesResponseDto> responseDtoList = new ArrayList<>();

        for (Notices notices : noticesList)
            responseDtoList.add(new NoticesResponseDto(notices));

        return responseDtoList;
    }
}
