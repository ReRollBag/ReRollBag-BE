package com.ReRollBag.domain.dto.Notices;

import com.ReRollBag.domain.entity.Notices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticesSaveRequestDto {
    String title;
    String content;

    public Notices toEntity() {
        return Notices.builder()
                .content(content)
                .title(title)
                .build();
    }
}
