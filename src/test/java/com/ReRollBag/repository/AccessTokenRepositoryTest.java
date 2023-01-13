package com.ReRollBag.repository;

import com.ReRollBag.config.RedisConfig;
import com.ReRollBag.domain.entity.AccessToken;
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
public class AccessTokenRepositoryTest {

    private static final Long AccessTokenValidTime = 1L;

    private static final AccessToken dummyToken = AccessToken.builder()
            .usersId("dummyUsersId")
            .accessToken("dummyRefreshToken")
            .expiredTime(1000000000L)
            .build();

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @Test
    @Order(1)
    @DisplayName("[Repository] Access Token 생성 테스트")
    @Rollback(value = false)
    void Repository_AccessToken_생성_테스트() {
        //given
        AccessToken accessToken = AccessToken.builder()
                .usersId("test@gmail.com")
                .accessToken("testAccessToken")
                .expiredTime(AccessTokenValidTime)
                .build();
        String expectedAccessToken = "testAccessToken";
        //when
        accessTokenRepository.save(accessToken);
        AccessToken target = accessTokenRepository.findById("test@gmail.com").orElseThrow(IllegalAccessError::new);

        //then
        assertThat(target.getAccessToken()).isEqualTo(expectedAccessToken);
    }

    @Test
    @Order(2)
    @DisplayName("[Repository] Access Token 잘못된 Key로 조회 테스트")
    void Repository_AccessToken_조회실패_테스트() {
        //given
        //when
        AccessToken invalidToken = accessTokenRepository.findById("InvalidPK").orElse(dummyToken);
        //then
        assertThat(invalidToken.getUsersId()).isEqualTo(dummyToken.getUsersId());
        assertThat(invalidToken.getAccessToken()).isEqualTo(dummyToken.getAccessToken());
        assertThat(invalidToken.getExpiredTime()).isEqualTo(dummyToken.getExpiredTime());
    }

    @Test
    @Order(3)
    @DisplayName("[Repository] Access Token Expiration 테스트")
    void Repository_AccessToken_만료_테스트() {
        //given
        String validUsersId = "test@gmail.com";
        //when
        try {
            Thread.sleep(AccessTokenValidTime*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AccessToken mustBeExpiredToken = accessTokenRepository.findById(validUsersId).orElse(dummyToken);
        //then
        assertThat(mustBeExpiredToken.getUsersId()).isEqualTo(dummyToken.getUsersId());
        assertThat(mustBeExpiredToken.getAccessToken()).isEqualTo(dummyToken.getAccessToken());
        assertThat(mustBeExpiredToken.getExpiredTime()).isEqualTo(dummyToken.getExpiredTime());
    }
}
