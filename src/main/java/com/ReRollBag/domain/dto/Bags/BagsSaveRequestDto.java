package com.ReRollBag.domain.dto.Bags;

import com.ReRollBag.domain.entity.Bags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BagsSaveRequestDto {
    private String countryCode;
    private String regionCode;

    public Bags toEntity() {
        String bagsId = countryCode + "_" + regionCode + "_";
        return Bags.builder()
                .bagsId(bagsId)
                .isRented(false)
                .whenIsRented(null)
                .build();
    }
}
