package com.ReRollBag.domain.dto.Tokens;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class AccessTokenResponseDto {
    private String accessToken;
}
