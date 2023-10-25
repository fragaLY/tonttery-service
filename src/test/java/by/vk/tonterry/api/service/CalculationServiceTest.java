/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonterry.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import by.vk.tonttery.api.service.CalculationService;
import by.vk.tonttery.configuration.lottery.PaymentProperties;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tags({@Tag("unit"), @Tag("calculation")})
class CalculationServiceTest {

  CalculationService service;

  @BeforeEach
  void setUp() {
    var properties = new PaymentProperties(1);
    service = new CalculationService(properties);
  }

  @DisplayName("Prize calculation parametrized tests")
  @ParameterizedTest
  @ValueSource(ints = {0, 1, 100, Integer.MAX_VALUE})
  void prizeParametrized(int input) {
    var expected = BigDecimal.valueOf(input)
        .multiply(BigDecimal.valueOf(100 - service.properties().commissionPercentage()));
    var actual = service.prize(input);
    assertEquals(expected, actual);
  }

}