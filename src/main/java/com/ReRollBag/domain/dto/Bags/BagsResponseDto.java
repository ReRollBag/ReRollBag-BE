package com.ReRollBag.domain.dto.Bags;

import com.ReRollBag.domain.entity.Bags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Getter
@Setter
@AllArgsConstructor
public class BagsResponseDto implements Comparable<BagsResponseDto> {
    private String bagsId;
    private boolean isRented;
    private String whenIsRented;
    private String rentingUsersId;

    public BagsResponseDto(Bags bags) {
        this.bagsId = bags.getBagsId();
        this.isRented = bags.isRented();
        this.whenIsRented = bags.getWhenIsRented().toString();
        this.rentingUsersId = "";
        if (bags.isRented() && bags.getRentingUsers() != null)
            this.rentingUsersId = bags.getRentingUsers().getUsersId();
        if (bags.isRented() && bags.getReturningUsers() != null)
            this.rentingUsersId = bags.getReturningUsers().getUsersId();
    }

    @Override
    public int compareTo(BagsResponseDto other) {
        LocalDateTime thisTime = LocalDateTime.parse(this.getWhenIsRented());
        LocalDateTime otherTime = LocalDateTime.parse(other.getWhenIsRented());
        if (thisTime.isBefore(otherTime)) return -1;
        return 1;
    }
}
