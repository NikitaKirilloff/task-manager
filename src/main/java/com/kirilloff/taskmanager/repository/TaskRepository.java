package com.kirilloff.taskmanager.repository;

import com.kirilloff.taskmanager.domain.entity.Task;
import com.kirilloff.taskmanager.domain.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, UUID> {
  List<Task> findByUserAndDueDateBetweenAndCompleted(User user, LocalDate start, LocalDate end, boolean completed);
}
