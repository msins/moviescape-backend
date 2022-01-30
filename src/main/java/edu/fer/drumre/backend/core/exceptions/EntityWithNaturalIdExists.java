package edu.fer.drumre.backend.core.exceptions;

import javax.persistence.EntityExistsException;

public class EntityWithNaturalIdExists extends EntityExistsException {

  public <T> EntityWithNaturalIdExists(Class clazz, T naturalId) {
    super(clazz.getSimpleName() + " with value '" + naturalId.toString() + "' already exists.");
  }

}
