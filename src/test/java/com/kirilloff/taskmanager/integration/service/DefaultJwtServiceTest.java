package com.kirilloff.taskmanager.integration.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kirilloff.taskmanager.domain.entity.User;
import com.kirilloff.taskmanager.integration.BaseIntegrationTest;
import com.kirilloff.taskmanager.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "auth.secret=mySecretKeymySecretKeymySecretKeymySecretKey",
    "auth.access-token.expiration=PT1H",
    "auth.refresh-token.expiration=PT2H"
})
class DefaultJwtServiceTest extends BaseIntegrationTest {

  @Autowired
  private JwtService jwtService;

  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    userDetails = new User(UUID.randomUUID(), "testUser", "password", "USER");
  }

  @Test
  void testGenerateAccessAndRefreshToken() {
    String accessToken = jwtService.generateAccessToken(userDetails.getUsername());

    assertThat(accessToken).isNotNull();
    assertThat(jwtService.extractUsername(accessToken)).isEqualTo(userDetails.getUsername());
    assertThat(jwtService.isTokenValid(accessToken, userDetails)).isTrue();

    String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

    assertThat(refreshToken).isNotNull();
    assertThat(jwtService.extractUsername(refreshToken)).isEqualTo(userDetails.getUsername());
    assertThat(jwtService.isTokenValid(refreshToken, userDetails)).isTrue();
  }

  @Test
  void testTokenValid() {
    String token = jwtService.generateAccessToken(userDetails.getUsername());

    assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
  }

  @Test
  void testTokenValidThrowExpiredJwtException() {
    String token = Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() - 1000))
        .signWith(Keys.hmacShaKeyFor("mySecretKeymySecretKeymySecretKeymySecretKey".getBytes()),
            Jwts.SIG.HS256)
        .compact();

    assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
        .isInstanceOf(ExpiredJwtException.class)
        .hasMessageContaining("JWT expired");
  }

  @Test
  void testExtractUsername() {
    String token = jwtService.generateAccessToken(userDetails.getUsername());

    String username = jwtService.extractUsername(token);

    assertThat(username).isEqualTo(userDetails.getUsername());
  }

  @Test
  void testExtractUsernameThrowJwtException() {
    String invalidToken = "invalid.token";

    assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
        .isInstanceOf(JwtException.class);
  }
}
