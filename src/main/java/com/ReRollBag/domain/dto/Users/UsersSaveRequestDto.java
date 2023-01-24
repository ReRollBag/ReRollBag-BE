package com.ReRollBag.domain.dto.Users;

import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsersSaveRequestDto {
    private String usersId;
    private String nickname;
    private String password;
    private String userRole;

    public Users toEntity() {
        //Setting Default Parameter
        if (this.userRole == null)
            userRole = "ROLE_USER";

        return Users.builder()
                .usersId(usersId)
                .nickname(nickname)
                .password(password)
                .userRole(UserRole.valueOf(userRole))
                .build();
    }
}
