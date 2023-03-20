package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.RentingMarkers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class RentingMarkersRepositoryTest {

    @Autowired
    RentingMarkersRepository rentingMarkersRepository;

    @Test
    @DisplayName("[Repository] 대여소 저장 테스트")
    void Repository_대여소저장_테스트() {
        RentingMarkers rentingMarkers = RentingMarkers.builder()
                .markersId(1L)
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .maxBagsNum(5)
                .currentBagsNum(5)
                .imageUrl("testImageUrl.com")
                .build();

        rentingMarkersRepository.save(rentingMarkers);
        RentingMarkers targetMarker = rentingMarkersRepository.findById(1L).get();

        assertThat(rentingMarkers.getMarkersId()).isEqualTo(1L);
        assertThat(rentingMarkers.getLatitude()).isEqualTo(12345.54321);
        assertThat(rentingMarkers.getLongitude()).isEqualTo(54321.12345);
        assertThat(rentingMarkers.getName()).isEqualTo("GS25 우만점");
        assertThat(rentingMarkers.getMaxBagsNum()).isEqualTo(5L);
        assertThat(rentingMarkers.getCurrentBagsNum()).isEqualTo(5L);
        assertThat(rentingMarkers.getImageUrl()).isEqualTo("testImageUrl.com");
    }
}
