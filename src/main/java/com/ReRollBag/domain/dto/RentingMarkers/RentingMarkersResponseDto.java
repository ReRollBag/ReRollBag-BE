package com.ReRollBag.domain.dto.RentingMarkers;

import com.ReRollBag.domain.entity.RentingMarkers;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class RentingMarkersResponseDto {
    private double latitude;
    private double longitude;
    private String name;
    private int maxBagsNum;
    private int currentBagsNum;

    public RentingMarkersResponseDto(RentingMarkers rentingMarkers) {
        this.latitude = rentingMarkers.getLatitude();
        this.longitude = rentingMarkers.getLongitude();
        this.name = rentingMarkers.getName();
        this.maxBagsNum = rentingMarkers.getMaxBagsNum();
        this.currentBagsNum = rentingMarkers.getCurrentBagsNum();
    }
}
