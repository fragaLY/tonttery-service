package by.vk.tonttery.api.exception;

import java.util.UUID;

/**
 * The exception that is thrown when the requested resource is not found. Related to the HTTP Status
 * Code 404.
 */
public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String className, UUID id) {
    this("The " + className + " with id [ = " + id + " ] was not found.");
  }
}
