package com.kirilloff.taskmanager.controller;

import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface TaskController {

  TaskResponseDTO getTask(@PathVariable UUID id);

  List<TaskResponseDTO> getTasks(@RequestParam LocalDate start, @RequestParam LocalDate end,
      @RequestParam Boolean completed);

  List<TaskResponseDTO> getTodayTasks(@RequestParam(required = false) Boolean completed);

  List<TaskResponseDTO> getWeekTasks(@RequestParam(required = false) Boolean completed);

  List<TaskResponseDTO> getMonthTasks(@RequestParam(required = false) Boolean completed);

  TaskResponseDTO createTask(@RequestBody TaskRequestDTO task);

  TaskResponseDTO updateTask(@PathVariable UUID id, @RequestBody TaskRequestDTO taskUpdateDTO);

  TaskResponseDTO toggleTaskCompletion(@PathVariable UUID id);

  void deleteTask(@PathVariable UUID id);
}
