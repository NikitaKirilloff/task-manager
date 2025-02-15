package com.kirilloff.taskmanager.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kirilloff.taskmanager.domain.request.UserRequestDTO;
import com.kirilloff.taskmanager.domain.response.AuthenticatedResponseDTO;
import com.kirilloff.taskmanager.integration.BaseIntegrationTest;
import com.kirilloff.taskmanager.repository.UserRepository;
import com.kirilloff.taskmanager.service.impl.DefaultUserDetailsService;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

class DefaultUserDetailsServiceTest extends BaseIntegrationTest {

  @Autowired
  private DefaultUserDetailsService userService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void testCreateNewUser() {
    UserRequestDTO request = new UserRequestDTO("newUser", "password123");

    AuthenticatedResponseDTO response = userService.createNewUser(request);

    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isNotEmpty();
    assertThat(response.getRefreshToken()).isNotEmpty();
    assertThat(userRepository.findByUsername("newUser")).isPresent();
  }

  @Test
  void testAuthenticateUser() {
    UserRequestDTO request = new UserRequestDTO("testUser", "password123");
    userService.createNewUser(request);

    AuthenticatedResponseDTO response = userService.authenticate(request);

    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isNotEmpty();
    assertThat(response.getRefreshToken()).isNotEmpty();
  }

  @Test
  void testAuthenticateWithWrongPassword() {
    UserRequestDTO request = new UserRequestDTO("wrongUser", "password123");
    userService.createNewUser(request);

    UserRequestDTO wrongPasswordRequest = new UserRequestDTO("wrongUser", "wrongPassword");

    assertThatThrownBy(() -> userService.authenticate(wrongPasswordRequest))
        .isInstanceOf(BadCredentialsException.class);
  }

  @Test
  void testRefreshToken() {
    UserRequestDTO request = new UserRequestDTO("refreshUser", "password123");
    AuthenticatedResponseDTO authResponse = userService.createNewUser(request);

    AuthenticatedResponseDTO refreshedResponse = userService.refreshToken(
        authResponse.getRefreshToken());

    assertThat(refreshedResponse).isNotNull();
    assertThat(refreshedResponse.getAccessToken()).isNotEmpty();
    assertThat(refreshedResponse.getRefreshToken()).isNotEmpty();
  }

  @Test
  void testIInvalidRefreshToken() {
    assertThatThrownBy(() -> userService.refreshToken("invalidRefreshToken"))
        .isInstanceOf(MalformedJwtException.class);
  }
}