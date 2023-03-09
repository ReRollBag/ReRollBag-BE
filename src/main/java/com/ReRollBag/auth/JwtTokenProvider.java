package com.ReRollBag.auth;

import com.ReRollBag.domain.dto.Tokens.AccessTokenResponseDto;
import com.ReRollBag.enums.TokenType;
import com.ReRollBag.exceptions.authExceptions.ReIssueBeforeAccessTokenExpiredException;
import com.ReRollBag.exceptions.authExceptions.TokenIsNullException;
import com.ReRollBag.service.CustomUserDetailService;
import com.ReRollBag.service.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@PropertySource("classpath:security.properties")
@Component
public class JwtTokenProvider {

    @Value("${jwtToken.secretKey}")
    private String secretKey;

    private final CustomUserDetailService userDetailService;
    private final RedisService redisService;

    private static long accessTokenValidTime = 5 * 60L;
    private static long refreshTokenValidTime = 3600 * 60L;

    public void setAccessTokenValidTime(Long time) {
        accessTokenValidTime = time;
    }

    public void setRefreshTokenValidTime(Long time) {
        refreshTokenValidTime = time;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Jwt Token 의 Subject 는 UID, Id 는 usersId 가 저장
    private String createToken(TokenType tokenType, String UID, String usersId) {

        long tokenValidTime;
        if (tokenType == TokenType.AccessToken) tokenValidTime = accessTokenValidTime;
        else tokenValidTime = refreshTokenValidTime;

        Claims claims = Jwts.claims().setSubject(UID);
        claims.setId(usersId);
        Date now = new Date();
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + tokenValidTime * 1000L)).signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public String createAccessToken(String UID, String usersId) {
        String accessToken = createToken(TokenType.AccessToken, UID, usersId);
        redisService.saveAccessToken(UID, accessToken, accessTokenValidTime);
        return accessToken;
    }

    public String createRefreshToken(String UID, String usersId) {
        String refreshToken = createToken(TokenType.RefreshToken, UID, usersId);
        redisService.saveRefreshToken(UID, refreshToken, refreshTokenValidTime);
        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(getUID(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUID(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUsersId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getId();
    }

    public String resolveToken(HttpServletRequest request) throws TokenIsNullException {
        String token = request.getHeader("Token");
        if (token == null) throw new TokenIsNullException();
        return token;
    }

    public boolean validateToken(String token) throws ReIssueBeforeAccessTokenExpiredException {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return !claims.getBody().getExpiration().before(new Date());
    }

    private void checkRefreshTokenIsExpired(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        claims.getBody().getExpiration().before(new Date());
    }

    private Boolean checkAccessTokenIsExpired(String token) {
        String usersId = getUID(token);
        return redisService.findAccessToken(usersId) == null;
    }

    public AccessTokenResponseDto reIssue(HttpServletRequest request) throws ReIssueBeforeAccessTokenExpiredException {
        String refreshToken = resolveToken(request);
        try {
            checkRefreshTokenIsExpired(refreshToken);
        } catch (MalformedJwtException | SignatureException e) {
            throw e;
        }

        if (!checkAccessTokenIsExpired(refreshToken))
            throw new ReIssueBeforeAccessTokenExpiredException();

        String UID = getUID(refreshToken);
        String usersId = getUsersId(refreshToken);

        String newAccessToken = createAccessToken(UID, usersId);
        return AccessTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .build();
    }

    private Boolean checkRefreshTokenExpirationBelowHalf(String refreshToken) {
        log.info("checkRefreshTokenExpirationBelowHalf");

        // Get Current Expiration Time of RefreshToken
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
        Date currentExpirationTime = claims.getBody().getExpiration();

        // Check currentExpirationTime is smaller than now + (refreshTokenValidTime/2) and return
        Date now = new Date();
        return currentExpirationTime.getTime() < refreshTokenValidTime * 1000L / 2 + now.getTime();
    }

    private String extendRefreshTokenExpiration(String refreshToken) {
        log.info("extendRefreshTokenExpiration");

        // Get UID from Token
        String UID = getUID(refreshToken);

        // Expand TokenValidTime in Redis
        redisService.extendRefreshTokenValidTime(UID, refreshTokenValidTime);

        // Expand TokenValidTime in Jwts
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
        Date now = new Date();
        claims.getBody().setExpiration(new Date(now.getTime() + refreshTokenValidTime * 1000L));

        // Create new RefreshToken
        return Jwts.builder()
                .setClaims(claims.getBody())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }
}
