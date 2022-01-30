package edu.fer.drumre.backend.core.networking;

import edu.fer.drumre.backend.user.exceptions.AuthorizationException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlingController {

  private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingController.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnhandledException(Exception exception) {
    logger.error(ExceptionUtils.getStackTrace(exception));
    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handleRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
    return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException exception) {
    return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(EntityExistsException.class)
  public ResponseEntity<ApiError> handleEntityExistsException(EntityExistsException exception) {
    return buildResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler(AuthorizationException.class)
  public ResponseEntity<ApiError> handleAuthorizationException(AuthorizationException exception) {
    return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
  }

  /*
   * === HIBERNATE/JPA SPECIFIC ===
   */

  @ExceptionHandler(TransactionSystemException.class)
  public ResponseEntity<ApiError> handle(TransactionSystemException exception) {
    if (exception.getCause().getClass() == RollbackException.class) {
      return handleRollbackException((RollbackException) exception.getCause());
    }
    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Test");
  }

  @ExceptionHandler(RollbackException.class)
  public ResponseEntity<ApiError> handleRollbackException(RollbackException exception) {
    if (exception.getCause().getClass() == ConstraintViolationException.class) {
      return handleConstraintViolation((ConstraintViolationException) exception.getCause());
    }
    return buildResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception) {
    return buildResponseEntity(
        HttpStatus.BAD_REQUEST,
        exception.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .toList()
            .toString()
    );
  }

  private ResponseEntity<ApiError> buildResponseEntity(HttpStatus status, String message) {
    ApiError error = new ApiError(
        status.getReasonPhrase(),
        message,
        status.value()
    );
    return new ResponseEntity<>(error, status);
  }
}
