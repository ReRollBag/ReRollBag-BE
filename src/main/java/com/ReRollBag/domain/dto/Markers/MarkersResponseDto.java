package com.ReRollBag.domain.dto.Markers;

import com.ReRollBag.domain.entity.Markers;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class MarkersResponseDto {
    private Long markersId;
    private String name;
    private String latitude;
    private String longitude;

    public MarkersResponseDto(Markers markers) {
        this.markersId = markers.getMarkersId();
        this.name = markers.getName();
        this.latitude = markers.getLatitude();
        this.longitude = markers.getLongitude();
    }
}
