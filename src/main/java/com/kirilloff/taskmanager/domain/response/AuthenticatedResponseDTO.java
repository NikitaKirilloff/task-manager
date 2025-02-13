package com.kirilloff.taskmanager.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedResponseDTO {
  private String description;
  private String accessToken;
  private String refreshToken;
}
