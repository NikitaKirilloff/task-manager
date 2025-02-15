package com.kirilloff.taskmanager.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.request.UserRequestDTO;
import com.kirilloff.taskmanager.domain.response.AuthenticatedResponseDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import com.kirilloff.taskmanager.integration.BaseIntegrationTest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class DefaultTaskControllerTest extends BaseIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  private static String accessToken;

  private static HttpHeaders headers;

  @BeforeEach
  void setUp() {
    if (accessToken == null) {
      AuthenticatedResponseDTO response = registerUser(
          "usernameForTaskTest", "passwordForTaskTest");

      accessToken = response.getAccessToken();
      headers = new HttpHeaders();
      headers.setBearerAuth(accessToken);
    }
  }

  @Test
  void testGetTaskWithValidId() {
    createAndVerifyTask("Название задачи", "Описание", false, LocalDate.now());
  }

  @Test
  void testGetTaskWithValidIdOfAnotherUser() {
    TaskResponseDTO createdTask = createAndVerifyTask(
        "Название задачи", "Описание", false, LocalDate.now());
    AuthenticatedResponseDTO authenticatedResponse = registerUser(
        "anotherUserGetTask", "passwordForAnotherUser");
    HttpEntity<TaskRequestDTO> entity = createHttpEntity(authenticatedResponse.getAccessToken());

    ResponseEntity<String> response = restTemplate.exchange(
            "/tasks/" + createdTask.getId(),
            HttpMethod.GET,
            entity,
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).contains(
        "Задача с id: " + createdTask.getId() + " не найдена");
  }

  @Test
  void testGetTasksWithValidDates() {
    TaskResponseDTO createdTask1 = createTask("testGetTasksWithValidDates1", "Описание", false,
        LocalDate.parse("2025-02-07"));
    TaskResponseDTO createdTask2 = createTask("testGetTasksWithValidDates2", "Описание", false,
        LocalDate.parse("2025-02-15"));
    TaskResponseDTO createdTask3 = createTask("testGetTasksWithValidDates3", "Описание", false,
        LocalDate.parse("2025-02-20"));
    String url = "/tasks?start=2025-02-01&end=2025-02-20&completed=false";
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<TaskResponseDTO>> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains(createdTask1, createdTask2, createdTask3);
  }

  @Test
  void testGetTasksWithInvalidDates() {
    String url = "/tasks?start=2025-02-10&end=2025-02-01&completed=true";
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Дата окончания не может быть раньше даты начала");
  }

  @Test
  void testGetTodayTasks() {
    TaskResponseDTO createdTask1 = createTask("testGetTodayTasks1", "Описание", true,
        LocalDate.now());
    TaskResponseDTO createdTask2 = createTask("testGetTodayTasks2", "Описание", true,
        LocalDate.now());
    String url = "/tasks/today?completed=true";
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<TaskResponseDTO>> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains(createdTask1, createdTask2);
  }

  @Test
  void testGetWeekTasks() {
    TaskResponseDTO createdTask1 = createTask("testGetWeekTasks1", "Описание", false,
        LocalDate.now().plusDays(6));
    TaskResponseDTO createdTask2 = createTask("testGetWeekTasks2", "Описание", false,
        LocalDate.now().plusDays(4));
    String url = "/tasks/week?completed=false";
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<TaskResponseDTO>> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains(createdTask1, createdTask2);
  }

  @Test
  void testGetMonthTasks() {
    TaskResponseDTO createdTask1 = createTask("testGetMonthTasks1", "Описание", false,
        LocalDate.now().plusDays(6));
    TaskResponseDTO createdTask2 = createTask("testGetMonthTasks2", "Описание", false,
        LocalDate.now().plusDays(26));
    TaskResponseDTO createdTask3 = createTask("testGetMonthTasks3", "Описание", false,
        LocalDate.now().plusDays(15));
    TaskResponseDTO createdTask4 = createTask("testGetMonthTasks4", "Описание", true,
        LocalDate.now().plusDays(15));
    String url = "/tasks/month?completed=false";
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<TaskResponseDTO>> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains(createdTask1, createdTask2, createdTask3);
    assertThat(response.getBody()).doesNotContain(createdTask4);
  }

  @Test
  void testCreateTaskWithValidData() {
    TaskResponseDTO createdTask = createAndVerifyTask("testCreateTaskWithValidData1", "Description", false,
        LocalDate.now().plusDays(1));
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(headers);

    ResponseEntity<TaskResponseDTO> response = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.GET,
        entity,
        TaskResponseDTO.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(createdTask).isEqualTo(response.getBody());
  }

  @Test
  void testCreateTaskWithInvalidData() {
    TaskRequestDTO taskRequest = new TaskRequestDTO("", "", true, null);
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(taskRequest, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/tasks", HttpMethod.POST, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Название должно содержать от 1 до 100 символов");
  }

  @Test
  void testUpdateTaskWithValidData() {
    TaskResponseDTO createdTask = createAndVerifyTask("Название задачи", "Описание", false, LocalDate.now());
    TaskRequestDTO taskRequest = new TaskRequestDTO("Новое название задачи", "Описание",
        true, LocalDate.now().plusDays(1));
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(taskRequest, headers);

    ResponseEntity<TaskResponseDTO> responseUpdatedTask = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.PUT,
        entity,
        TaskResponseDTO.class);

    assertThat(responseUpdatedTask.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseUpdatedTask.getBody())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(taskRequest);
  }

  @Test
  void testUpdateTaskWithInvalidData() {
    TaskResponseDTO createdTask = createAndVerifyTask("Название задачи", "Описание", false, LocalDate.now());
    TaskRequestDTO taskRequest = new TaskRequestDTO("", "Описание",
        true, LocalDate.now().plusDays(1));
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(taskRequest, headers);

    ResponseEntity<String> responseUpdatedTask = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.PUT,
        entity,
        String.class);

    assertThat(responseUpdatedTask.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    taskRequest = new TaskRequestDTO("Новое название задачи", "Описание",
        true, LocalDate.now().plusDays(1));
    entity = new HttpEntity<>(taskRequest, headers);

    responseUpdatedTask = restTemplate.exchange(
        "/tasks/" + UUID.randomUUID(),
        HttpMethod.PUT,
        entity,
        String.class);

    assertThat(responseUpdatedTask.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testUpdateTaskOfAnotherUser() {
    TaskResponseDTO createdTask = createAndVerifyTask("Название задачи", "Описание", true, LocalDate.now());
    TaskRequestDTO taskRequest = new TaskRequestDTO("Новое название задачи", "Описание",
        true, LocalDate.now().plusDays(1));
    AuthenticatedResponseDTO authenticatedResponse = registerUser(
        "anotherUserUpdateTask", "passwordForAnotherUser");
    String newAccessToken = authenticatedResponse.getAccessToken();
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(taskRequest,
        createHttpEntity(newAccessToken).getHeaders());

    ResponseEntity<String> responseUpdatedTask = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.PUT,
        entity,
        String.class);

    assertThat(responseUpdatedTask.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(responseUpdatedTask.getBody()).contains(
        "Задача с id: " + createdTask.getId() + " не найдена");
  }

  @Test
  void testToggleTaskCompletion() {
    TaskResponseDTO createdTask = createAndVerifyTask("Название задачи", "Описание", false, LocalDate.now());
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(headers);

    ResponseEntity<TaskResponseDTO> response =
        restTemplate.exchange(
            "/tasks/" + createdTask.getId() + "/complete",
            HttpMethod.PATCH,
            entity,
            TaskResponseDTO.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().isCompleted()).isEqualTo(!createdTask.isCompleted());
  }

  @Test
  void testToggleTaskCompletionOfAnotherUser() {
    TaskResponseDTO createdTask = createAndVerifyTask("Название задачи", "Описание", false, LocalDate.now());
    AuthenticatedResponseDTO authenticatedResponse = registerUser(
        "anotherUserToggleTaskCompletion", "passwordForAnotherUser");
    HttpEntity<TaskRequestDTO> entity = createHttpEntity(authenticatedResponse.getAccessToken());

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/tasks/" + createdTask.getId() + "/complete",
            HttpMethod.PATCH,
            entity,
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).contains(
        "Задача с id: " + createdTask.getId() + " не найдена");
  }

  @Test
  void testDeleteTask() {
    TaskResponseDTO createdTask = createAndVerifyTask("Название задачи", "Описание", false, LocalDate.now());
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(headers);

    ResponseEntity<Void> response = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.DELETE,
        entity,
        Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<String> responseWithoutTask = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.GET,
        entity,
        String.class
    );

    assertThat(responseWithoutTask.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(responseWithoutTask.getBody()).contains(
        "Задача с id: " + createdTask.getId() + " не найдена");
  }

  @Test
  void testDeleteTaskOfAnotherUser() {
    TaskResponseDTO createdTask = createAndVerifyTask("Название задачи", "Описание", false, LocalDate.now());
    AuthenticatedResponseDTO authenticatedResponse = registerUser(
        "anotherUserDeleteTask", "passwordForAnotherUser");
    HttpEntity<TaskRequestDTO> entity = createHttpEntity(authenticatedResponse.getAccessToken());

    ResponseEntity<String> responseException = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.DELETE,
        entity,
        String.class);

    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(responseException.getBody()).contains(
        "Задача с id: " + createdTask.getId() + " не найдена");
  }

  private AuthenticatedResponseDTO registerUser(String username, String password) {
    ResponseEntity<AuthenticatedResponseDTO> response = restTemplate.postForEntity(
        "/auth/register",
        new UserRequestDTO(username, password),
        AuthenticatedResponseDTO.class
    );
    return response.getBody();
  }

  private HttpEntity<TaskRequestDTO> createHttpEntity(String accessToken) {
    HttpHeaders newHeaders = new HttpHeaders();
    newHeaders.setBearerAuth(accessToken);
    return new HttpEntity<>(newHeaders);
  }

  private TaskResponseDTO createAndVerifyTask(String title, String description, boolean completed, LocalDate dueDate) {
    TaskResponseDTO createdTask = createTask(title, description, completed, dueDate);
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(headers);

    ResponseEntity<TaskResponseDTO> response = restTemplate.exchange(
        "/tasks/" + createdTask.getId(),
        HttpMethod.GET,
        entity,
        TaskResponseDTO.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(createdTask).isEqualTo(response.getBody());

    return createdTask;
  }

  private TaskResponseDTO createTask(String title, String description, boolean completed,
      LocalDate date) {
    TaskRequestDTO taskRequest = new TaskRequestDTO(title, description, completed, date);
    HttpEntity<TaskRequestDTO> entity = new HttpEntity<>(taskRequest, headers);
    return restTemplate.exchange(
        "/tasks", HttpMethod.POST, entity, TaskResponseDTO.class).getBody();
  }
}
