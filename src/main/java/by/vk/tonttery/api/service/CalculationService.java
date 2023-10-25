/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api.service;

import by.vk.tonttery.configuration.lottery.PaymentProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * The service for calculation of the prize.
 *
 * @param properties - the properties of tonttery. Should not be null.
 */
@Component
public record CalculationService(
    @NotNull(message = "The tonttery properties should not be null") PaymentProperties properties) {

  private static final int HUNDRED = 100;

  /**
   * The method for calculation of the prize.
   *
   * @param size - the size of lottery attenders. Should be positive.
   * @return the prize of lottery. Should be positive.
   */
  @PositiveOrZero(message = "The minimal prize should be at least zero")
  public BigDecimal prize(
      @PositiveOrZero(message = "The minimal prize should be at least zero") int size) {
    return BigDecimal.valueOf(size)
        .multiply(BigDecimal.valueOf(HUNDRED - properties.commissionPercentage()));
  }
}
