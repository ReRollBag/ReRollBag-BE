package com.ReRollBag.repository;

import com.ReRollBag.config.RedisConfig;
import com.ReRollBag.domain.entity.CertificationNumber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@EnableRedisRepositories
@Import(RedisConfig.class)
public class CertificationNumberRepositoryTest {

    private static final Long certificationNumberValidTime = 1L;
    @Autowired
    CertificationNumberRepository certificationNumberRepository;
    private CertificationNumber certificationNumber;

    @BeforeEach
    void createCertificationNumber() {
        certificationNumber = CertificationNumber.builder()
                .usersId("dummyUsersId")
                .certificationNumber(1234)
                .expiredTime(1L)
                .build();
        certificationNumberRepository.save(certificationNumber);
    }

    @AfterEach
    void deleteCertificationNumber() {
        if (certificationNumberRepository.existsById(certificationNumber.getUsersId()))
            certificationNumberRepository.deleteById(certificationNumber.getUsersId());
        certificationNumber = null;
    }

    @Test
    @DisplayName("[Repository] Certification Number 생성 테스트")
    void Repository_CertificationNumber_생성_테스트() {
        CertificationNumber certificationNumber = CertificationNumber.builder()
                .usersId("dummyUsersId")
                .certificationNumber(1234)
                .expiredTime(1L)
                .build();
        certificationNumberRepository.save(certificationNumber);
        CertificationNumber target = certificationNumberRepository.findById(certificationNumber.getUsersId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find CertificationNumber at CertificationNumberRepositoryTest.Repository_CertificationNumber_생성_테스트"));

        assertThat(target.getUsersId()).isEqualTo(certificationNumber.getUsersId());
        assertThat(target.getCertificationNumber()).isEqualTo(certificationNumber.getCertificationNumber());
    }

    @Test
    @DisplayName("[Repository] CertificationNumber 잘못된 Key로 조회 테스트")
    void Repository_CertificationNumber_조회실패_테스트() {
        assertThrows(IllegalArgumentException.class, () -> certificationNumberRepository.findById("InvalidId").orElseThrow(() -> new IllegalArgumentException()));
    }

    @Test
    @DisplayName("[Repository] Certification Number Expiration 테스트")
    void Repository_CertificationNumber_만료_테스트() throws InterruptedException {
        Thread.sleep(certificationNumberValidTime * 1000);
        assertThrows(IllegalArgumentException.class, () -> certificationNumberRepository.findById(certificationNumber.getUsersId()).orElseThrow(() -> new IllegalArgumentException()));
    }
}
