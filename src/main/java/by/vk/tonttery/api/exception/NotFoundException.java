/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

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
