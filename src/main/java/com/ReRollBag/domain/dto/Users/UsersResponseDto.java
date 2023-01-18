package com.ReRollBag.domain.dto.Users;

import com.ReRollBag.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
public class UsersResponseDto {
    public String usersId;
    public String nickname;

    @Builder
    public UsersResponseDto(Users users) {
        this.usersId = users.getUsersId();
        this.nickname = users.getNickname();
    }
}
