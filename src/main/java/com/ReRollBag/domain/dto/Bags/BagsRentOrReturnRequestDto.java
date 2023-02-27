package com.ReRollBag.domain.dto.Bags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BagsRentOrReturnRequestDto {
    private String usersId;
    private String bagsId;
}
