package com.kirilloff.taskmanager.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.kirilloff.taskmanager.domain.request.UserRequestDTO;
import com.kirilloff.taskmanager.domain.response.AuthenticatedResponseDTO;
import com.kirilloff.taskmanager.integration.BaseIntegrationTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class DefaultAuthControllerTest extends BaseIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testRegisterWithValidData() {
    ResponseEntity<AuthenticatedResponseDTO> response =
        createAuthenticatedResponseDTO("validuser1", "validpassword");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getAccessToken()).isNotNull();
    assertThat(response.getBody().getRefreshToken()).isNotNull();
  }

  @Test
  void testRegisterWithInvalidUsername() {
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/auth/register",
        new UserRequestDTO("ab", "validpassword"),
        String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Имя пользователя должно быть не менее 3 символов");
  }

  @Test
  void testRegisterWithInvalidPassword() {
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/auth/register",
        new UserRequestDTO("validuser", "short"),
        String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Пароль должен быть не менее 8 символов");
  }

  @Test
  void testRegisterWithInvalidUsernameAndPassword() {
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/auth/register",
        new UserRequestDTO("ab", "short"),
        String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Имя пользователя должно быть не менее 3 символов");
    assertThat(response.getBody()).contains("Пароль должен быть не менее 8 символов");
  }

  @Test
  void testRegisterWithDuplicateUsername() {
    UserRequestDTO userRequestDTO = new UserRequestDTO();
    userRequestDTO.setUsername("duplicateuser");
    userRequestDTO.setPassword("validpassword");

    restTemplate.postForEntity("/auth/register", userRequestDTO, AuthenticatedResponseDTO.class);

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/auth/register",
        userRequestDTO,
        String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Пользователь с таким именем уже существует");
  }

  @Test
  void testRefreshTokenWithValidToken() {
    ResponseEntity<AuthenticatedResponseDTO> response =
        createAuthenticatedResponseDTO("validuser2", "validpassword");

    ResponseEntity<AuthenticatedResponseDTO> responseWithNewToken =
        restTemplate.postForEntity("/auth/refresh",
        Map.of("refreshToken", response.getBody().getRefreshToken()),
        AuthenticatedResponseDTO.class);

    assertThat(responseWithNewToken.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseWithNewToken.getBody()).isNotNull();
  }

  @Test
  void testRefreshTokenWithInvalidToken() {
    Map<String, String> invalidRequest = Map.of("refreshToken", "invalidToken123");

    ResponseEntity<String> response = restTemplate.postForEntity("/auth/refresh", invalidRequest, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Неверный формат JWT токена");

    response = restTemplate.postForEntity("/auth/refresh", new HashMap<String, String>(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testLoginWithValidCredentials() {
    createAuthenticatedResponseDTO("validuser3", "validpassword");

    ResponseEntity<AuthenticatedResponseDTO> responseLogin =
        restTemplate.postForEntity("/auth/login",
            new UserRequestDTO("validuser3", "validpassword"),
            AuthenticatedResponseDTO.class);

    assertThat(responseLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseLogin.getBody()).isNotNull();
    assertThat(responseLogin.getBody().getAccessToken()).isNotNull();
    assertThat(responseLogin.getBody().getRefreshToken()).isNotNull();
    assertThat(responseLogin.getBody().getDescription()).isEqualTo("Token issued");
  }

  @Test
  void testLoginWithInvalidPassword() {
    createAuthenticatedResponseDTO("validuser5", "password");

    ResponseEntity<String> response = restTemplate.postForEntity("/auth/login",
        new UserRequestDTO("validuser5", "wrongpassword"),
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testLoginWithNonExistentUser() {
    ResponseEntity<String> response = restTemplate.postForEntity("/auth/login",
        new UserRequestDTO("nonexistentuser", "validpassword"),
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testLoginWithMissingUsername() {
    UserRequestDTO missingUsernameUser = new UserRequestDTO();
    missingUsernameUser.setPassword("validpassword");

    ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", missingUsernameUser, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testLoginWithMissingPassword() {
    UserRequestDTO missingPasswordUser = new UserRequestDTO();
    missingPasswordUser.setUsername("validuser");

    ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", missingPasswordUser, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }


  private ResponseEntity<AuthenticatedResponseDTO> createAuthenticatedResponseDTO(String username, String password) {
    UserRequestDTO userRequestDTO = new UserRequestDTO();
    userRequestDTO.setUsername(username);
    userRequestDTO.setPassword(password);

    return restTemplate.postForEntity(
        "/auth/register",
        userRequestDTO,
        AuthenticatedResponseDTO.class
    );
  }
}