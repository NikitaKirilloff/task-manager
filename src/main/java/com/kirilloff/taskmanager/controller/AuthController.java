package com.kirilloff.taskmanager.controller;

import com.kirilloff.taskmanager.domain.request.UserRequestDTO;
import com.kirilloff.taskmanager.domain.response.AuthenticatedResponseDTO;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthController {

  ResponseEntity<AuthenticatedResponseDTO> register(@RequestBody UserRequestDTO userRequestDTO);

  ResponseEntity<AuthenticatedResponseDTO> refreshToken(@RequestBody Map<String, String> request);

  ResponseEntity<AuthenticatedResponseDTO> login(@RequestBody UserRequestDTO userRequestDTO);
}
