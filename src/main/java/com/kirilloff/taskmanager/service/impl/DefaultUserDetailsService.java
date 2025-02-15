package com.kirilloff.taskmanager.service.impl;

import com.kirilloff.taskmanager.domain.entity.User;
import com.kirilloff.taskmanager.domain.request.UserRequestDTO;
import com.kirilloff.taskmanager.domain.response.AuthenticatedResponseDTO;
import com.kirilloff.taskmanager.exception.InvalidRefreshToken;
import com.kirilloff.taskmanager.exception.UserAlreadyExistsException;
import com.kirilloff.taskmanager.repository.UserRepository;
import com.kirilloff.taskmanager.service.JwtService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Пользователь '"
            + username + "' не найден"));
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
    );
  }

  public AuthenticatedResponseDTO createNewUser(UserRequestDTO userRequestDTO) {
    if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
    }

    User user = new User();
    user.setUsername(userRequestDTO.getUsername());
    user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
    user.setRole("USER");

    userRepository.save(user);

    return generateAuthResponse(user);
  }

  public AuthenticatedResponseDTO authenticate(UserRequestDTO userRequestDTO) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userRequestDTO.getUsername(),
            userRequestDTO.getPassword()
        )
    );
    var user = userRepository.findByUsername(userRequestDTO.getUsername()).orElseThrow();
    return generateAuthResponse(user);
  }

  public AuthenticatedResponseDTO refreshToken(String refreshToken) {
    String username = jwtService.extractUsername(refreshToken);

    if (username != null) {
      var user = userRepository.findByUsername(username)
          .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

      if (jwtService.isTokenValid(refreshToken, user)) {
        return generateAuthResponse(user);
      }
    }
    throw new InvalidRefreshToken("Invalid Refresh Token");
  }

  private AuthenticatedResponseDTO generateAuthResponse(User user) {
    var accessToken = jwtService.generateAccessToken(user.getUsername());
    var refreshToken = jwtService.generateRefreshToken(user.getUsername());
    return new AuthenticatedResponseDTO("Token issued", accessToken, refreshToken);
  }
}
