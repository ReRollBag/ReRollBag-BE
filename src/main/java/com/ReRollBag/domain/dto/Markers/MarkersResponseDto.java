package com.ReRollBag.domain.dto.Markers;

import com.ReRollBag.domain.entity.Markers;
import com.ReRollBag.enums.MarkerType;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class MarkersResponseDto {
    private Long markersId;
    private String name;
    private double latitude;
    private double longitude;

    private String markerType;

    public MarkersResponseDto(Markers markers) {
        this.markersId = markers.getMarkersId();
        this.name = markers.getName();
        this.latitude = markers.getLatitude();
        this.longitude = markers.getLongitude();
        this.markerType = markers.getMarkerType().toString();
    }
}
