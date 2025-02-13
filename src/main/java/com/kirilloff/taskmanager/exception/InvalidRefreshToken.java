package com.kirilloff.taskmanager.exception;

public class InvalidRefreshToken extends RuntimeException {

  public InvalidRefreshToken(String message) {
    super(message);
  }
}
