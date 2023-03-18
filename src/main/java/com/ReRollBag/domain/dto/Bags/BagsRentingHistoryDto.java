package com.ReRollBag.domain.dto.Bags;

import com.ReRollBag.domain.entity.Bags;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BagsRentingHistoryDto {
    private String bagsId;
    private LocalDateTime whenIsRented;
    private LocalDateTime whenIsReturned;

    public BagsRentingHistoryDto(Bags bags) {
        this.bagsId = bags.getBagsId();
        this.whenIsRented = bags.getWhenIsRented();
        this.whenIsReturned = LocalDateTime.now();
    }

}
