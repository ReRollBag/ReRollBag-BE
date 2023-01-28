package com.ReRollBag.domain.dto.Bags;

import com.ReRollBag.domain.entity.Bags;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BagsResponseDto {
    private String bagsId;
    private boolean isRented;
    private LocalDateTime whenIsRented;
    private String rentingUsersId;

    public BagsResponseDto(Bags bags) {
        this.bagsId = bags.getBagsId();
        this.isRented = bags.isRented();
        this.whenIsRented = bags.getWhenIsRented();
        if (bags.isRented())
            this.rentingUsersId = bags.getRentingUsers().getUsersId();
    }
}
