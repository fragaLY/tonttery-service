package by.vk.tonttery.configuration.lottery;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("tonttery.payments")
public record TontteryProperties(
    @Min(value = 1, message = "The minimal commission for tonttery should be 1 percent")
    @Max(value = 99, message = "The maximum commission for tonttery should be 10 percent")
    int commissionPercentage
) {

}
