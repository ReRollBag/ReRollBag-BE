package com.ReRollBag.domain.dto.ReturningMarkers;

import com.ReRollBag.domain.entity.ReturningMarkers;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ReturningMarkersResponseDto {

    private double latitude;
    private double longitude;
    private String name;

    private String imageUrl;

    public ReturningMarkersResponseDto(ReturningMarkers returningMarkers) {
        this.latitude = returningMarkers.getLatitude();
        this.longitude = returningMarkers.getLongitude();
        this.name = returningMarkers.getName();
        this.imageUrl = returningMarkers.getImageUrl();
    }
}
