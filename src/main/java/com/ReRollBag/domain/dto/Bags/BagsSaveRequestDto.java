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
        String region = countryCode + "_" + regionCode + "_";
        return Bags.builder()
                .bagsId(region)
                .isRented(false)
                .whenIsRented(null)
                .region(region)
                .build();
    }
}
