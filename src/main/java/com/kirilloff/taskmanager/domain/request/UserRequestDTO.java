package com.kirilloff.taskmanager.domain.request;

import jakarta.validation.constraints.Size;
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
public class UserRequestDTO {

  @Size(message = "Имя пользователя должно быть не менее 3 символов", min = 3, max = 50)
  private String username;

  @Size(message = "Пароль должен быть не менее 8 символов", min = 8, max = 50)
  private String password;
}
