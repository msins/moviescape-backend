package edu.fer.drumre.backend.user.exceptions;

public class LoginFailed extends AuthorizationException {

  public LoginFailed(String message) {
    super(message);
  }
}
