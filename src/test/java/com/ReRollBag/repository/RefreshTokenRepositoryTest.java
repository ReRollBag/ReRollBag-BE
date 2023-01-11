package com.ReRollBag.repository;

import com.ReRollBag.config.RedisConfig;
import com.ReRollBag.domain.entity.RefreshToken;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@EnableRedisRepositories
@Import(RedisConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RefreshTokenRepositoryTest {

    private static final Long RefreshTokenValidTime = 5L;

    private static final RefreshToken dummyToken = RefreshToken.builder()
            .usersId("dummyUsersId")
            .refreshToken("dummyRefreshToken")
            .expiredTime(1000000000L)
            .build();

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @Order(1)
    @DisplayName("[Repository] Refresh Token 생성 테스트")
    @Rollback(value = false)
    void Repository_RefreshToken_생성_테스트() {
        //given
        RefreshToken refreshToken = RefreshToken.builder()
                .usersId("test@gmail.com")
                .refreshToken("testRefreshToken")
                .expiredTime(RefreshTokenValidTime)
                .build();

        String expectedRefreshToken = "testRefreshToken";

        //when
        refreshTokenRepository.save(refreshToken);
        RefreshToken target = refreshTokenRepository.findById("test@gmail.com").orElseThrow(IllegalArgumentException::new);

        //then
        assertThat(target.getRefreshToken()).isEqualTo(expectedRefreshToken);
    }

    @Test
    @Order(2)
    @DisplayName("[Repository] Refresh Token 잘못된 Key로 조회 테스트")
    void Repository_RefreshToken_조회실패_테스트() {
        //given
        //when
        RefreshToken invalidToken = refreshTokenRepository.findById("InvalidPK").orElse(dummyToken);
        //then
        assertThat(invalidToken.getUsersId()).isEqualTo(dummyToken.getUsersId());
        assertThat(invalidToken.getRefreshToken()).isEqualTo(dummyToken.getRefreshToken());
        assertThat(invalidToken.getExpiredTime()).isEqualTo(dummyToken.getExpiredTime());
    }

    @Test
    @Order(3)
    @DisplayName("[Repository] Refresh Token Expiration 테스트")
    void Repository_RefreshToken_만료_테스트() {
        //given
        String validUsersId = "test@gmail.com";
        //when
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        RefreshToken mustBeExpiredToken = refreshTokenRepository.findById(validUsersId).orElse(dummyToken);
        //then
        assertThat(mustBeExpiredToken.getUsersId()).isEqualTo(dummyToken.getUsersId());
        assertThat(mustBeExpiredToken.getRefreshToken()).isEqualTo(dummyToken.getRefreshToken());
        assertThat(mustBeExpiredToken.getExpiredTime()).isEqualTo(dummyToken.getExpiredTime());
    }
}
