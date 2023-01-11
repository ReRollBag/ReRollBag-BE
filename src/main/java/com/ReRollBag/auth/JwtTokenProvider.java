package com.ReRollBag.auth;

import com.ReRollBag.service.CustomUserDetailService;
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

    private static long accessTokenValidTime = 30 * 60 * 1000L;
    private static long refreshTokenValidTime = 3600 * 60 * 1000L;

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
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createAccessToken (String usersId) {
        return createToken(TokenType.AccessToken, usersId);
    }

    public String createRefreshToken (String usersId) {
        return createToken(TokenType.RefreshToken, usersId);
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(getUsersId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsersId (String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken (HttpServletRequest request) {
        return request.getHeader("Token");
    }

    public String resolveAccessToken (HttpServletRequest request) {
        return request.getHeader("AccessToken");
    }

    public String resolveRefreshToken (HttpServletRequest request) {
        return request.getHeader("RefreshToken");
    }

    public boolean validateToken (String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            return false;
        }
    }

}
