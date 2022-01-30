package edu.fer.drumre.backend.core.exceptions;

import javax.persistence.EntityNotFoundException;

public class EntityWithIdNotFound extends EntityNotFoundException {

  public <T> EntityWithIdNotFound(Class clazz, T nonExistingId) {
    super(clazz.getSimpleName() + " with id '" + nonExistingId.toString() + "' doesn't exist.");
  }
}
