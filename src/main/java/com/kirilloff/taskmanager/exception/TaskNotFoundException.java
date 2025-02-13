package com.kirilloff.taskmanager.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

  public TaskNotFoundException(UUID id) {
    super("Задача с id: " + id + " не найдена");
  }
}
