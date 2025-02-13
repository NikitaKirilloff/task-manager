package com.kirilloff.taskmanager.controller.impl;

import com.kirilloff.taskmanager.controller.TaskController;
import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import com.kirilloff.taskmanager.service.TaskService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class DefaultTaskController implements TaskController {

  private final TaskService service;

  @GetMapping("/{id}")
  public TaskResponseDTO getTask(@PathVariable UUID id) {
    return service.getTaskById(id);
  }

  @GetMapping
  public List<TaskResponseDTO> getTasks(@RequestParam LocalDate start, @RequestParam LocalDate end, @RequestParam Boolean completed) {
    return service.getTasks(start, end, completed);
  }

  @GetMapping("/today")
  public List<TaskResponseDTO> getTodayTasks(@RequestParam(required = false) Boolean completed) {
    LocalDate today = LocalDate.now();
    return service.getTasks(today, today, completed);
  }

  @GetMapping("/week")
  public List<TaskResponseDTO> getWeekTasks(@RequestParam(required = false) Boolean completed) {
    LocalDate today = LocalDate.now();
    LocalDate end = today.plusDays(7);
    return service.getTasks(today, end, completed);
  }

  @GetMapping("/month")
  public List<TaskResponseDTO> getMonthTasks(@RequestParam(required = false) Boolean completed) {
    LocalDate today = LocalDate.now();
    LocalDate end = today.plusMonths(1);
    return service.getTasks(today, end, completed);
  }

  @PostMapping
  public TaskResponseDTO createTask(@RequestBody TaskRequestDTO task) {
    return service.createTask(task);
  }

  @PutMapping("/{id}")
  public TaskResponseDTO updateTask(@PathVariable UUID id, @RequestBody TaskRequestDTO taskUpdateDTO) {
    return service.updateTask(id, taskUpdateDTO);
  }

  @PatchMapping("/{id}/complete")
  public TaskResponseDTO toggleTaskCompletion(@PathVariable UUID id) {
    return service.toggleTaskCompletion(id);
  }

  @DeleteMapping("/{id}")
  public void deleteTask(@PathVariable UUID id) {
    service.deleteTask(id);
  }
}
