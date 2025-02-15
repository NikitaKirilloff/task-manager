package com.kirilloff.taskmanager.exception;

import io.jsonwebtoken.MalformedJwtException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleIllegalArgumentException(TaskNotFoundException ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(InvalidRefreshToken.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleIllegalArgumentException(InvalidRefreshToken ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Ошибка валидации");

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));

    response.put("errors", errors);
    return response;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return Map.of("message", ex.getMessage());
  }

  @ExceptionHandler(MalformedJwtException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleMalformedJwtException() {
    return "Неверный формат JWT токена";
  }
}
