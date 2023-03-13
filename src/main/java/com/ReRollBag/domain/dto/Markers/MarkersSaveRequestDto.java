package com.ReRollBag.domain.dto.Markers;

import com.ReRollBag.domain.entity.Markers;
import com.ReRollBag.enums.MarkerType;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkersSaveRequestDto {
    private String name;
    private double latitude;
    private double longitude;

    private String markerType;

    public Markers toEntity() {
        return Markers.builder()
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .markerType(MarkerType.valueOf(markerType))
                .build();
    }
}
