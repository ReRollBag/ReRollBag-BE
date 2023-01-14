package com.ReRollBag.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("AccessToken")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessToken {
    @Id
    private String usersId;
    private String accessToken;

    @TimeToLive
    private Long expiredTime;
}
