package com.kirilloff.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
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
  public String handleIllegalArgumentException(UserAlreadyExistsException ex) {
    return ex.getMessage();
  }
}
