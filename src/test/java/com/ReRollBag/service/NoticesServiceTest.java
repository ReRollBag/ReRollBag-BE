package com.ReRollBag.service;

import com.ReRollBag.domain.dto.Notices.NoticesResponseDto;
import com.ReRollBag.domain.dto.Notices.NoticesSaveRequestDto;
import com.ReRollBag.domain.entity.Notices;
import com.ReRollBag.repository.NoticesRepository;
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
public class NoticesServiceTest {
    @InjectMocks
    private NoticesService noticesService;

    @Mock
    private NoticesRepository noticesRepository;

    @Test
    @DisplayName("[Service] 공지 저장 테스트")
    public void Service_공지저장_테스트() throws Exception {
        //given
        NoticesSaveRequestDto requestDto = NoticesSaveRequestDto.builder()
                .title("testTitle")
                .content("testContent")
                .build();

        Notices notices = requestDto.toEntity();

        //when
        when(noticesRepository.save(any())).thenReturn(notices);
        NoticesResponseDto responseDto = noticesService.save(requestDto);

        //then
        assertThat(responseDto.getTitle()).isEqualTo("testTitle");
        assertThat(responseDto.getContent()).isEqualTo("testContent");
    }

    @Test
    @DisplayName("[Service] 가장 최근 공지 조회 테스트")
    public void Service_가장최근공지조회_테스트() throws Exception {
        //given
        NoticesSaveRequestDto requestDto1 = NoticesSaveRequestDto.builder()
                .title("testTitle1")
                .content("testContent1")
                .build();

        NoticesSaveRequestDto requestDto2 = NoticesSaveRequestDto.builder()
                .title("testTitle2")
                .content("testContent2")
                .build();

        Notices notices1 = requestDto1.toEntity();
        Notices notices2 = requestDto2.toEntity();

        List<Notices> noticesList = new ArrayList<>();
        noticesList.add(notices1);
        noticesList.add(notices2);

        //when
        when(noticesRepository.findAll()).thenReturn(noticesList);
        NoticesResponseDto responseDto = noticesService.getLastNotices();

        //then
        assertThat(responseDto.getTitle()).isEqualTo("testTitle2");
        assertThat(responseDto.getContent()).isEqualTo("testContent2");
    }

    @Test
    @DisplayName("[Service] 모든 공지 조회 테스트")
    public void Service_모든공지조회_테스트() throws Exception {
        //given
        NoticesSaveRequestDto requestDto1 = NoticesSaveRequestDto.builder()
                .title("testTitle1")
                .content("testContent1")
                .build();

        NoticesSaveRequestDto requestDto2 = NoticesSaveRequestDto.builder()
                .title("testTitle2")
                .content("testContent2")
                .build();

        Notices notices1 = requestDto1.toEntity();
        Notices notices2 = requestDto2.toEntity();

        List<Notices> noticesList = new ArrayList<>();
        noticesList.add(notices1);
        noticesList.add(notices2);

        //when
        when(noticesRepository.findAll()).thenReturn(noticesList);
        List<NoticesResponseDto> responseDtoList = noticesService.getAllNotices();

        //then
        assertThat(responseDtoList.size()).isEqualTo(2);
        assertThat(responseDtoList.get(0).getTitle()).isEqualTo("testTitle1");
        assertThat(responseDtoList.get(0).getContent()).isEqualTo("testContent1");
        assertThat(responseDtoList.get(1).getTitle()).isEqualTo("testTitle2");
        assertThat(responseDtoList.get(1).getContent()).isEqualTo("testContent2");
    }
}
