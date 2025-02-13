package com.kirilloff.taskmanager.service.impl;

import com.kirilloff.taskmanager.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultJwtService implements JwtService {

  @Value("${auth.secret}")
  private String secretKey;

  @Value("${auth.access-token.expiration}")
  private Duration accessTokenExpiration;

  @Value("${auth.refresh-token.expiration}")
  private Duration refreshTokenExpiration;

  @Override
  public String generateAccessToken(String username) {
    return generateToken(username, accessTokenExpiration);
  }

  @Override
  public String generateRefreshToken(String username) {
    return generateToken(username, refreshTokenExpiration);
  }

  @Override
  public String extractUsername(String token) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private String generateToken(String username, Duration expiration) {
    return Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), Jwts.SIG.HS256)
        .compact();
  }

  private boolean isTokenExpired(String token) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration()
        .before(new Date());
  }
}