package com.ReRollBag.domain.dto;

import com.ReRollBag.domain.entity.Users;

public class UsersSaveRequestDto {
    public String usersId;
    public String nickname;
    public String password;

    public Users toEntity() {
        return Users.builder()
                .usersId(usersId)
                .nickname(nickname)
                .build();
    }
}
