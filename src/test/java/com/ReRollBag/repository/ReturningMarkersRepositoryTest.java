package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.ReturningMarkers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class ReturningMarkersRepositoryTest {

    @Autowired
    ReturningMarkersRepository returningMarkersRepository;

    @Test
    @DisplayName("[Repository] 반납소 저장 테스트")
    void Repository_반납소저장_테스트() {
        ReturningMarkers returningMarkers = ReturningMarkers.builder()
                .markersId(1L)
                .latitude(12345.54321)
                .longitude(54321.12345)
                .name("GS25 우만점")
                .imageUrl("testImageUrl.com")
                .build();

        returningMarkersRepository.save(returningMarkers);
        ReturningMarkers targetMarker = returningMarkersRepository.findById(1L).get();

        assertThat(targetMarker.getMarkersId()).isEqualTo(1L);
        assertThat(targetMarker.getLatitude()).isEqualTo(12345.54321);
        assertThat(targetMarker.getLongitude()).isEqualTo(54321.12345);
        assertThat(targetMarker.getName()).isEqualTo("GS25 우만점");
        assertThat(targetMarker.getImageUrl()).isEqualTo("testImageUrl.com");
    }
}
