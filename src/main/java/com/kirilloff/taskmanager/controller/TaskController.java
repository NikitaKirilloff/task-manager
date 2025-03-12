package com.kirilloff.taskmanager.controller;

import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface TaskController {

  ResponseEntity<TaskResponseDTO> getTask(@PathVariable UUID id);

  ResponseEntity<List<TaskResponseDTO>> getTasks(LocalDate start, LocalDate end, Boolean completed);

  ResponseEntity<List<TaskResponseDTO>> getWeekTasks(
      @RequestParam(required = false) Boolean completed);


  ResponseEntity<List<TaskResponseDTO>> getMonthTasks(
      @RequestParam(required = false) Boolean completed);

  ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO task);

  ResponseEntity<TaskResponseDTO> updateTask(@PathVariable UUID id,
      @Valid @RequestBody TaskRequestDTO taskUpdateDTO);

  ResponseEntity<TaskResponseDTO> toggleTaskCompletion(@PathVariable UUID id);

  ResponseEntity<Void> deleteTask(@PathVariable UUID id);
}
