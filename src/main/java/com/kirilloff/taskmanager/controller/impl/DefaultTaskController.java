package com.kirilloff.taskmanager.controller.impl;

import com.kirilloff.taskmanager.controller.TaskController;
import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import com.kirilloff.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class DefaultTaskController implements TaskController {

  private final TaskService service;

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponseDTO> getTask(@PathVariable UUID id) {
    return ResponseEntity.ok(service.getTaskById(id));
  }

  @GetMapping
  public ResponseEntity<List<TaskResponseDTO>> getTasks(
      @RequestParam @NotNull(message = "Дата начала не может быть пустой") LocalDate start,
      @RequestParam @NotNull(message = "Дата окончания не может быть пустой") LocalDate end,
      @RequestParam(required = false) Boolean completed) {
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
    }
    return ResponseEntity.ok(service.getTasks(start, end, completed));
  }

  @GetMapping("/today")
  public ResponseEntity<List<TaskResponseDTO>> getTodayTasks(
      @RequestParam(required = false) Boolean completed) {
    LocalDate today = LocalDate.now();
    return ResponseEntity.ok(service.getTasks(today, today, completed));
  }

  @GetMapping("/week")
  public ResponseEntity<List<TaskResponseDTO>> getWeekTasks(
      @RequestParam(required = false) Boolean completed) {
    LocalDate today = LocalDate.now();
    LocalDate end = today.plusDays(7);
    return ResponseEntity.ok(service.getTasks(today, end, completed));
  }

  @GetMapping("/month")
  public ResponseEntity<List<TaskResponseDTO>> getMonthTasks(
      @RequestParam(required = false) Boolean completed) {
    LocalDate today = LocalDate.now();
    LocalDate end = today.plusMonths(1);
    return ResponseEntity.ok(service.getTasks(today, end, completed));
  }

  @PostMapping
  public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO task) {
    return ResponseEntity.ok(service.createTask(task));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable UUID id,
      @Valid @RequestBody TaskRequestDTO taskUpdateDTO) {
    return ResponseEntity.ok(service.updateTask(id, taskUpdateDTO));
  }

  @PatchMapping("/{id}/complete")
  public ResponseEntity<TaskResponseDTO> toggleTaskCompletion(@PathVariable UUID id) {
    return ResponseEntity.ok(service.toggleTaskCompletion(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
    service.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}