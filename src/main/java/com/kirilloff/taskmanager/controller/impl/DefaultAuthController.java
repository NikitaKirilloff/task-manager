package com.kirilloff.taskmanager.controller.impl;

import com.kirilloff.taskmanager.domain.request.UserDto;
import com.kirilloff.taskmanager.domain.response.AuthenticatedResponseDTO;
import com.kirilloff.taskmanager.controller.AuthController;
import com.kirilloff.taskmanager.service.impl.DefaultUserDetailsService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class DefaultAuthController implements AuthController {

  private final DefaultUserDetailsService userDetailsService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticatedResponseDTO> register(@RequestBody UserDto userDto) {
    return ResponseEntity.ok(userDetailsService.createNewUser(userDto));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthenticatedResponseDTO> refreshToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refreshToken");
    return ResponseEntity.ok(userDetailsService.refreshToken(refreshToken));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticatedResponseDTO> login(@RequestBody UserDto userDto) {
    return ResponseEntity.ok(userDetailsService.authenticate(userDto));
  }
}
