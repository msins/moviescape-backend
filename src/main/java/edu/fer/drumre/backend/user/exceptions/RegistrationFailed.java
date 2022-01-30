package edu.fer.drumre.backend.user.exceptions;

public class RegistrationFailed extends AuthorizationException {

  public RegistrationFailed(String message) {
    super(message);
  }
}
