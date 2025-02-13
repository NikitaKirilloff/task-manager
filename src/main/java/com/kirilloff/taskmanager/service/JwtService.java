package com.kirilloff.taskmanager.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

  String generateAccessToken(String username);

  String extractUsername(String token);

  boolean isTokenValid(String token, UserDetails userDetails);

  String generateRefreshToken(String username);
}
