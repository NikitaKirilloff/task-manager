package com.kirilloff.taskmanager.domain.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskRequestDTO {

  @Size(min = 1, max = 100, message = "Название должно содержать от 1 до 100 символов")
  private String title;

  @Size(max = 1000, message = "Описание не может быть длиннее 500 символов")
  private String description;

  private boolean completed;

  private LocalDate dueDate;
}
