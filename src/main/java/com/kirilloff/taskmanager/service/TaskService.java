package com.kirilloff.taskmanager.service;

import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskService {

  TaskResponseDTO getTaskById(UUID id);

  List<TaskResponseDTO> getTasks(LocalDate start, LocalDate end, Boolean completed);

  TaskResponseDTO createTask(TaskRequestDTO task);

  TaskResponseDTO updateTask(UUID id, TaskRequestDTO taskUpdateDTO);

  TaskResponseDTO toggleTaskCompletion(UUID id);

  void deleteTask(UUID id);
}
