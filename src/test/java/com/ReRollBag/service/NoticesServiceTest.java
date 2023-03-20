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
}
