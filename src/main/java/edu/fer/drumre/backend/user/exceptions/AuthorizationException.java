package edu.fer.drumre.backend.user.exceptions;

public abstract class AuthorizationException extends RuntimeException {

  public AuthorizationException(String message) {
    super(message);
  }
}
