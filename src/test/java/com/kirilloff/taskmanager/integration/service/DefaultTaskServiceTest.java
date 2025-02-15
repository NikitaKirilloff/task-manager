package com.kirilloff.taskmanager.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kirilloff.taskmanager.domain.entity.Task;
import com.kirilloff.taskmanager.domain.entity.User;
import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import com.kirilloff.taskmanager.exception.TaskNotFoundException;
import com.kirilloff.taskmanager.integration.BaseIntegrationTest;
import com.kirilloff.taskmanager.repository.TaskRepository;
import com.kirilloff.taskmanager.repository.UserRepository;
import com.kirilloff.taskmanager.service.TaskService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class DefaultTaskServiceTest extends BaseIntegrationTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    taskRepository.deleteAll();
    userRepository.deleteAll();

    user = new User();
    user.setUsername("testUser");
    user.setPassword("password123");
    user.setRole("USER");
    user = userRepository.save(user);

    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .roles("USER")
        .build();
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
    );
  }

  @Test
  void testCreateTask() {
    TaskRequestDTO request = new TaskRequestDTO("Test Task", "Test Description",
        false, LocalDate.now().plusDays(1));

    TaskResponseDTO result = taskService.createTask(request);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Task");
    assertThat(result.getDescription()).isEqualTo("Test Description");
    assertThat(result.getDueDate()).isEqualTo(LocalDate.now().plusDays(1));
    assertThat(result.isCompleted()).isFalse();
    assertThat(taskRepository.findAll()).hasSize(1);
  }

  @Test
  void testGetTaskById() {
    Task task = new Task();
    task.setTitle("Test Task testGetTaskById");
    task.setDescription("Test Description");
    task.setDueDate(LocalDate.now().plusDays(10));
    task.setCompleted(false);
    task.setUser(user);
    task = taskRepository.save(task);

    TaskResponseDTO result = taskService.getTaskById(task.getId());

    assertThat(result).isNotNull();
    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(task);
  }

  @Test
  void testGetTaskByIdNotFound() {
    UUID taskId = UUID.randomUUID();

    assertThatThrownBy(() -> taskService.getTaskById(taskId))
        .isInstanceOf(TaskNotFoundException.class)
        .hasMessageContaining(taskId.toString());
  }

  @Test
  void testGetTasks() {
    Task task1 = new Task();
    task1.setTitle("Task 1");
    task1.setDescription("Description 1");
    task1.setDueDate(LocalDate.now().plusDays(1));
    task1.setCompleted(false);
    task1.setUser(user);
    taskRepository.save(task1);

    Task task2 = new Task();
    task2.setTitle("Task 2");
    task2.setDescription("Description 2");
    task2.setDueDate(LocalDate.now().plusDays(2));
    task2.setCompleted(true);
    task2.setUser(user);
    taskRepository.save(task2);

    List<TaskResponseDTO> result = taskService.getTasks(
        LocalDate.now(),
        LocalDate.now().plusDays(7),
        false
    );

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Task 1");
  }

  @Test
  void testUpdateTask() {
    Task task = new Task();
    task.setTitle("Old Title");
    task.setDescription("Old Description");
    task.setDueDate(LocalDate.now().plusDays(1));
    task.setCompleted(false);
    task.setUser(user);
    task = taskRepository.save(task);

    TaskRequestDTO request = new TaskRequestDTO(
        "New Title",
        "New Description",
        true,
        LocalDate.now().plusDays(2)

    );

    TaskResponseDTO result = taskService.updateTask(task.getId(), request);

    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(request);
  }

  @Test
  void testUpdateTaskNotFound() {
    TaskRequestDTO request = new TaskRequestDTO(
        "New Title",
        "New Description",
        true,
        LocalDate.now().plusDays(2)

    );
    UUID taskId = UUID.randomUUID();

    assertThatThrownBy(() -> taskService.updateTask(taskId, request))
        .isInstanceOf(TaskNotFoundException.class)
        .hasMessageContaining(taskId.toString());
  }

  @Test
  void testToggleTaskCompletion() {
    Task task = new Task();
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setDueDate(LocalDate.now().plusDays(1));
    task.setCompleted(false);
    task.setUser(user);
    task = taskRepository.save(task);

    TaskResponseDTO result = taskService.toggleTaskCompletion(task.getId());

    assertThat(result).isNotNull();
    assertThat(result.isCompleted()).isTrue();
  }

  @Test
  void testDeleteTask() {
    Task task = new Task();
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setDueDate(LocalDate.now().plusDays(1));
    task.setCompleted(false);
    task.setUser(user);
    task = taskRepository.save(task);

    taskService.deleteTask(task.getId());

    assertThat(taskRepository.findById(task.getId())).isEmpty();
  }

  @Test
  void testDeleteTaskNotFound() {
    UUID taskId = UUID.randomUUID();

    assertThatThrownBy(() -> taskService.deleteTask(taskId))
        .isInstanceOf(TaskNotFoundException.class)
        .hasMessageContaining(taskId.toString());
  }
}
