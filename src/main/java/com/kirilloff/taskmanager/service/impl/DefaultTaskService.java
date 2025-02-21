package com.kirilloff.taskmanager.service.impl;

import com.kirilloff.taskmanager.domain.entity.Task;
import com.kirilloff.taskmanager.domain.entity.User;
import com.kirilloff.taskmanager.domain.mapper.TaskMapper;
import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import com.kirilloff.taskmanager.exception.TaskNotFoundException;
import com.kirilloff.taskmanager.repository.TaskRepository;
import com.kirilloff.taskmanager.service.TaskService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultTaskService implements TaskService {

  private final DefaultUserDetailsService userDetailsService;

  private final TaskRepository taskRepository;

  private final TaskMapper taskMapper;

  @Override
  public TaskResponseDTO getTaskById(UUID id) {
    User user = userDetailsService.getCurrentUser();
    Task task = taskRepository.findById(id)
        .filter(t -> t.getUser().equals(user))
        .orElseThrow(() -> new TaskNotFoundException(id));

    return taskMapper.toDto(task);
  }

  @Override
  public List<TaskResponseDTO> getTasks(LocalDate start, LocalDate end, Boolean completed) {
    User user = userDetailsService.getCurrentUser();
    return taskRepository.findByUserAndDueDateBetweenAndCompleted(user, start, end, completed)
        .stream()
        .map(taskMapper::toDto)
        .toList();
  }

  @Override
  public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
    User user = userDetailsService.getCurrentUser();

    Task task = new Task();
    task.setTitle(taskRequestDTO.getTitle());
    task.setDescription(taskRequestDTO.getDescription());
    task.setDueDate(taskRequestDTO.getDueDate());
    task.setCompleted(taskRequestDTO.isCompleted());
    task.setUser(user);

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public TaskResponseDTO updateTask(UUID id, TaskRequestDTO taskUpdateDTO) {
    User user = userDetailsService.getCurrentUser();

    Task task = taskRepository.findById(id)
        .filter(t -> t.getUser().equals(user))
        .orElseThrow(() -> new TaskNotFoundException(id));

    taskMapper.updateTaskFromDto(taskUpdateDTO, task);
    task.setDueDate(taskUpdateDTO.getDueDate());
    task = taskRepository.save(task);

    return taskMapper.toDto(task);
  }

  @Override
  public TaskResponseDTO toggleTaskCompletion(UUID id) {
    User user = userDetailsService.getCurrentUser();

    Task task = taskRepository.findById(id)
        .filter(t -> t.getUser().equals(user))
        .orElseThrow(() -> new TaskNotFoundException(id));

    task.setCompleted(!task.isCompleted());
    taskRepository.save(task);

    return taskMapper.toDto(task);
  }

  @Override
  public void deleteTask(UUID id) {
    User user = userDetailsService.getCurrentUser();

    Task task = taskRepository.findById(id)
        .filter(t -> t.getUser().equals(user))
        .orElseThrow(() -> new TaskNotFoundException(id));

    taskRepository.delete(task);
  }
}
