package com.ReRollBag.domain.dto.Users;

import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersSaveRequestDto {
    private String usersId;
    private String name;
    private String idToken;
    private String userRole;

    public UsersSaveRequestDto(String usersId, String name, String idToken) {
        this.usersId = usersId;
        this.name = name;
        this.idToken = idToken;
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


