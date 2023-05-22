package com.ReRollBag.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


@RedisHash("CertificationNumber")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificationNumber {
    @Id
    private String usersId;
    private Long certificationNumber;

    @TimeToLive
    private Long expiredTime;
}
