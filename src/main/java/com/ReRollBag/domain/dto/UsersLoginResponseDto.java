package com.ReRollBag.domain.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersLoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
