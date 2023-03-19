package com.ReRollBag.domain.dto.RentingMarkers;

import com.ReRollBag.domain.entity.RentingMarkers;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class RentingMarkersSaveRequestDto {
    private double latitude;
    private double longitude;
    private String name;
    private int maxBagsNum;
    private int currentBagsNum;

    private String imageUrl;

    public RentingMarkers toEntity() {
        return RentingMarkers.builder()
                .latitude(latitude)
                .longitude(longitude)
                .name(name)
                .maxBagsNum(maxBagsNum)
                .currentBagsNum(currentBagsNum)
                .imageUrl(imageUrl)
                .build();
    }
}
