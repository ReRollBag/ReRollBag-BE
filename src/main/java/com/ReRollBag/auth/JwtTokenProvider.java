package com.ReRollBag.auth;

import com.ReRollBag.exceptions.authExceptions.ReIssueBeforeAccessTokenExpiredException;
import com.ReRollBag.exceptions.authExceptions.TokenIsNullException;
import com.ReRollBag.repository.AccessTokenRepository;
import com.ReRollBag.repository.RefreshTokenRepository;
import com.ReRollBag.service.CustomUserDetailService;
import com.ReRollBag.service.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Log4j2
@RequiredArgsConstructor
@PropertySource("classpath:security.properties")
@Component
public class JwtTokenProvider {

    @Value("${jwtToken.secretKey}")
    private String secretKey;

    private final CustomUserDetailService userDetailService;
    private final RedisService redisService;
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

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

    private String createToken(TokenType tokenType, String usersId) {

        long tokenValidTime;
        if (tokenType == TokenType.AccessToken) tokenValidTime = accessTokenValidTime;
        else tokenValidTime = refreshTokenValidTime;

        Claims claims = Jwts.claims().setSubject(usersId);
        claims.put("usersId", usersId);
        claims.put("tokenType", tokenType);

        Date now = new Date();
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + tokenValidTime * 1000L)).signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public String createAccessToken(String usersId) {
        String accessToken = createToken(TokenType.AccessToken, usersId);
        redisService.saveAccessToken(usersId, accessToken, accessTokenValidTime);
        return accessToken;
    }

    public String createRefreshToken(String usersId) {
        String refreshToken = createToken(TokenType.RefreshToken, usersId);
        redisService.saveRefreshToken(usersId, refreshToken, refreshTokenValidTime);
        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(getUsersId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUsersId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    private TokenType getTokenType(String token) {
        String tokenType = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("tokenType").toString();
        if (tokenType.equals("AccessToken")) return TokenType.AccessToken;
        return TokenType.RefreshToken;
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
        String usersId = getUsersId(token);
        return redisService.findAccessToken(usersId) == null;
    }

    public String reIssue(HttpServletRequest request) throws ReIssueBeforeAccessTokenExpiredException {
        String refreshToken = resolveToken(request);
        try {
            checkRefreshTokenIsExpired(refreshToken);
        } catch (MalformedJwtException | SignatureException e) {
            throw e;
        }
        if (!checkAccessTokenIsExpired(refreshToken)) throw new ReIssueBeforeAccessTokenExpiredException();

        String usersId = getUsersId(refreshToken);
        return createAccessToken(usersId);
    }

}
