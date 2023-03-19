package com.ReRollBag.domain.dto.ReturningMarkers;

import com.ReRollBag.domain.entity.ReturningMarkers;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReturningMarkersSaveRequestDto {
    private double latitude;
    private double longitude;
    private String name;

    private String imageUrl;

    public ReturningMarkers toEntity() {
        return ReturningMarkers.builder()
                .latitude(latitude)
                .longitude(longitude)
                .name(name)
                .imageUrl(imageUrl)
                .build();
    }
}
