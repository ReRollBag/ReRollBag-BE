package com.ReRollBag.domain.dto.Users;

import com.ReRollBag.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsersSaveRequestDto {
    private String usersId;
    private String nickname;
    private String password;

    public Users toEntity() {
        return Users.builder()
                .usersId(usersId)
                .nickname(nickname)
                .password(password)
                .build();
    }
}
