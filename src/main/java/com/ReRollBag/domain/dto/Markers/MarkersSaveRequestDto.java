package com.ReRollBag.domain.dto.Markers;

import com.ReRollBag.domain.entity.Markers;
import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkersSaveRequestDto {
    private String name;
    private String latitude;
    private String longitude;

    public Markers toEntity() {
        return Markers.builder()
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
