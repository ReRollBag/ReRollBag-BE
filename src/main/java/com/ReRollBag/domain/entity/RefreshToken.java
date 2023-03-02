package com.ReRollBag.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("RefreshToken")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    private String usersId;
    private String refreshToken;

    @TimeToLive
    private Long expiredTime;

    public void extendRefreshTokenValidTime(Long time) {
        this.expiredTime += time;
    }
}
