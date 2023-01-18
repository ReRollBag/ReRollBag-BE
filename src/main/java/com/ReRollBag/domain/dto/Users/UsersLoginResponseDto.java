package com.ReRollBag.domain.dto.Users;

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
