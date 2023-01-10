package com.ReRollBag.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UsersLoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
