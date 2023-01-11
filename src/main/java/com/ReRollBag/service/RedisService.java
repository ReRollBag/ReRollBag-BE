package com.ReRollBag.service;

import com.ReRollBag.domain.entity.AccessToken;
import com.ReRollBag.domain.entity.RefreshToken;
import com.ReRollBag.exceptions.tokenExceptions.AccessTokenExpiredException;
import com.ReRollBag.exceptions.tokenExceptions.RefreshTokenExpiredException;
import com.ReRollBag.repository.AccessTokenRepository;
import com.ReRollBag.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class RedisService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;

    public void saveRefreshToken (String key, String value, Long duration) {
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(value)
                .usersId(key)
                .expiredTime(duration)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    public void saveAccessToken (String key, String value, Long duration) {
        AccessToken accessToken = AccessToken.builder()
                .accessToken(value)
                .usersId(key)
                .expiredTime(duration)
                .build();
        accessTokenRepository.save(accessToken);
    }

    public String findAccessToken(String key) throws AccessTokenExpiredException {
        AccessToken accessToken = accessTokenRepository.findById(key)
                .orElseThrow(AccessTokenExpiredException::new);
        return accessToken.getAccessToken();
    }

    public String findRefreshToken(String key) throws RefreshTokenExpiredException {
        RefreshToken refreshToken = refreshTokenRepository.findById(key)
                .orElseThrow(RefreshTokenExpiredException::new);
        return refreshToken.getRefreshToken();
    }

    public void deleteRefreshToken (String key) {
        refreshTokenRepository.deleteById(key);
    }
    public void deleteAccessToken (String key) { accessTokenRepository.deleteById(key);}
}
