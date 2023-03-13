package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Markers;
import com.ReRollBag.enums.MarkerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class MarkersRepositoryTest {

    @Autowired
    MarkersRepository markersRepository;

    @Test
    @DisplayName("[Repository] 마커 저장 테스트")
    void Repository_마커저장_테스트() {

        Markers markers = Markers.builder()
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("testMarker")
                .markerType(MarkerType.Rent)
                .build();

        markersRepository.save(markers);
        Markers targetMarker = markersRepository.findById(1L).get();

        Long expectedId = 1L;
        Double expectedLatitude = 12345.54321;
        Double expectedLongitude = 54321.12345;
        String expectedName = "testMarker";

        assertThat(targetMarker.getMarkersId()).isEqualTo(expectedId);
        assertThat(targetMarker.getLatitude()).isEqualTo(expectedLatitude);
        assertThat(targetMarker.getLongitude()).isEqualTo(expectedLongitude);
        assertThat(targetMarker.getName()).isEqualTo(expectedName);
        assertThat(targetMarker.getMarkerType()).isEqualTo(MarkerType.Rent);
    }
}
