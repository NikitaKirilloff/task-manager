package com.kirilloff.taskmanager.controller;

import com.kirilloff.taskmanager.domain.request.UserDto;
import com.kirilloff.taskmanager.domain.response.AuthenticatedResponseDTO;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthController {

  ResponseEntity<AuthenticatedResponseDTO> register(@RequestBody UserDto userDto);

  ResponseEntity<AuthenticatedResponseDTO> refreshToken(@RequestBody Map<String, String> request);

  ResponseEntity<AuthenticatedResponseDTO> login(@RequestBody UserDto userDto);
}
