package com.teamchallenge.marketplace.common.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String REFRESH_EMAIL_PREFIX = "refreshEmail_";
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken_";

    @Value("${spring.security.jwt.refresh-token-time}")
    private int timeoutRefreshToken;
    @Value("${spring.security.jwt.secret}")
    private String secretKey;
    @Value("${spring.security.jwt.expiration}")
    private Long expirationTime;

    private final RedisTemplate<String, String> redisTemplate;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails){
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateAccessToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails
    ){
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(@NotNull String userEmail) {
        String token = UUID.randomUUID().toString();
        String oldToken = redisTemplate.opsForValue().getAndSet(REFRESH_EMAIL_PREFIX + userEmail, token);
        if (oldToken != null){
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + oldToken);}
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + token, userEmail, timeoutRefreshToken, TimeUnit.DAYS);
        return token;
    }

    public String getEmailByRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue()
                .get(REFRESH_TOKEN_PREFIX + refreshToken);
    }
}
