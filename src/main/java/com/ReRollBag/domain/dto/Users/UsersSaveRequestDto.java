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
    private String name;
    private String idToken;
    private String userRole;

    public UsersSaveRequestDto(String usersId, String name, String idToken) {
        new UsersSaveRequestDto(
                usersId,
                name,
                idToken,
                null
        );
    }

    public Users toEntity() {
        //Setting Default Parameter
        if (this.userRole == null || this.userRole.equals(""))
            userRole = "ROLE_USER";

        return Users.builder()
                .usersId(usersId)
                .name(name)
                .userRole(UserRole.valueOf(userRole))
                .build();
    }
}


