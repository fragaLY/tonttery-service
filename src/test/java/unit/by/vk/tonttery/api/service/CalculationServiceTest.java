package unit.by.vk.tonttery.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import by.vk.tonttery.api.service.CalculationService;
import by.vk.tonttery.configuration.lottery.TontteryProperties;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tags({@Tag("unit"), @Tag("calculation")})
class CalculationServiceTest {

  CalculationService service;

  @BeforeEach
  void setUp() {
    var properties = new TontteryProperties(1);
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

  @DisplayName("Prize calculation when input incorrect tests")
  @Test
  void prizeWhenInputIncorrect() {
    assertEquals(BigDecimal.ONE, service.prize(-1));
  }

}