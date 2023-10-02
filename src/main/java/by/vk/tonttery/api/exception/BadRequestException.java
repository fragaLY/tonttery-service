/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api.exception;

/**
 * The exception that is thrown when the request is malformed. Related to the HTTP Status Code 400.
 */
public class BadRequestException extends RuntimeException {

  public BadRequestException(String message) {
    super(message);
  }

}
