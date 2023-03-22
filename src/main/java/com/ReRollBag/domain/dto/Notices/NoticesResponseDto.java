package com.ReRollBag.domain.dto.Notices;

import com.ReRollBag.domain.entity.Notices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticesResponseDto {
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public NoticesResponseDto(Notices notices) {
        this.title = notices.getTitle();
        this.content = notices.getContent();
        this.createdAt = notices.getCreatedAt();
        this.updatedAt = notices.getUpdatedAt();
    }
}
