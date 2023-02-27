package com.ReRollBag.domain.dto.Bags;

import com.ReRollBag.domain.entity.Bags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BagsResponseDto {
    private String bagsId;
    private boolean isRented;
    private String whenIsRented;
    private String rentingUsersId;

    public BagsResponseDto(Bags bags) {
        this.bagsId = bags.getBagsId();
        this.isRented = bags.isRented();
        this.whenIsRented = bags.getWhenIsRented().toString();
        this.rentingUsersId = "";
        if (bags.isRented())
            this.rentingUsersId = bags.getRentingUsers().getUsersId();
    }
}
