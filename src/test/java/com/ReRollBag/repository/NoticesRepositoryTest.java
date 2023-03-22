package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Notices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class NoticesRepositoryTest {

    @Autowired
    NoticesRepository noticesRepository;

    @Test
    @DisplayName("[Repository] 공지 저장 테스트")
    void Repository_공지저장_테스트() {
        LocalDateTime now = LocalDateTime.now();
        Notices notices = Notices.builder()
                .content("testContent")
                .title("testTitle")
                .build();

        noticesRepository.save(notices);
        Notices targetNotices = noticesRepository.findById(1L).get();

        assertThat(targetNotices.getContent()).isEqualTo("testContent");
        assertThat(targetNotices.getTitle()).isEqualTo("testTitle");
        assertThat(targetNotices.getCreatedAt()).isAfter(now);
        assertThat(targetNotices.getUpdatedAt()).isAfter(now);
    }

}
