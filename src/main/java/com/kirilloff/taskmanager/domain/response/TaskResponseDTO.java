package com.kirilloff.taskmanager.domain.response;

import java.time.LocalDate;
import java.util.UUID;
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
public class TaskResponseDTO {
  private UUID id;
  private String title;
  private String description;
  private boolean completed;
  private LocalDate dueDate;
}
