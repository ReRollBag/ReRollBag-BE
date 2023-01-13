package com.ReRollBag.auth;

import antlr.Token;
import com.ReRollBag.domain.entity.AccessToken;
import com.ReRollBag.domain.entity.RefreshToken;
import com.ReRollBag.exceptions.tokenExceptions.TokenIsNullException;
import com.ReRollBag.repository.AccessTokenRepository;
import com.ReRollBag.repository.RefreshTokenRepository;
import com.ReRollBag.service.CustomUserDetailService;
import com.ReRollBag.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    private static final long accessTokenValidTime =   5 * 60L;
    private static final long refreshTokenValidTime = 3600 * 60L;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String createToken (TokenType tokenType, String usersId) {

        long tokenValidTime;
        if (tokenType == TokenType.AccessToken) tokenValidTime = accessTokenValidTime;
        else tokenValidTime = refreshTokenValidTime;

        Claims claims = Jwts.claims().setSubject(usersId);
        claims.put("usersId", usersId);
        claims.put("tokenType", tokenType);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime*1000L))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createAccessToken (String usersId) {
        String accessToken = createToken(TokenType.AccessToken, usersId);
        redisService.saveAccessToken(usersId, accessToken, accessTokenValidTime);
        return accessToken;
    }

    public String createRefreshToken (String usersId) {
        String refreshToken = createToken(TokenType.RefreshToken, usersId);
        redisService.saveRefreshToken(usersId, refreshToken, refreshTokenValidTime);
        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(getUsersId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsersId (String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken (HttpServletRequest request) throws TokenIsNullException {
        String token = request.getHeader("Token");
        if (token == null) throw new TokenIsNullException();
        return token;
    }

    public String resolveAccessToken (HttpServletRequest request) {
        return request.getHeader("AccessToken");
    }

    public String resolveRefreshToken (HttpServletRequest request) {
        return request.getHeader("RefreshToken");
    }

    public boolean validateToken (String jwtToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        return !claims.getBody().getExpiration().before(new Date());
    }

}
