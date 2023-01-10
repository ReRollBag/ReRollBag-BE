package com.ReRollBag.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsersLoginRequestDto {
    private String usersId;
    private String password;
}
