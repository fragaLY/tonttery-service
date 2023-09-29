package by.vk.tonttery.api.service;

import by.vk.tonttery.configuration.lottery.TontteryProperties;
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
    @NotNull(message = "The tonttery properties should not be null") TontteryProperties properties) {

  private static final int HUNDRED = 100;

  /**
   * The method for calculation of the prize.
   *
   * @param size - the size of lottery attenders. Should be positive.
   *
   * @return the prize of lottery. Should be positive.
   */
  @PositiveOrZero(message = "The minimal prize should be at least zero")
  public BigDecimal prize(int size) {
    return BigDecimal.valueOf(size)
        .multiply(BigDecimal.valueOf(HUNDRED - properties.commissionPercentage()));
  }
}
