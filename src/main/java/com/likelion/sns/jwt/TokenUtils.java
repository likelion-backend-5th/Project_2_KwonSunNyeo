package com.likelion.sns.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class TokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;

    public TokenUtils(@Value("${jwt.secret}") String jwtSecret) {
        this.signingKey = Keys
                .hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    public String generateToken(UserDetails userDetails) {
        log.info("#log# 사용자 [{}] JWT 생성 시작", userDetails.getUsername());
        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));
        log.info("#log# 사용자 [{}] JWT 생성 완료", userDetails.getUsername());
        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }
}
