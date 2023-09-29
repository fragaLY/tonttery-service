package by.vk.tonttery;

import by.vk.tonttery.configuration.lottery.TontteryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {TontteryProperties.class})
public class Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    final var context = SpringApplication.run(Application.class, args);
    final var properties = context.getBean(BuildProperties.class);
    LOGGER.info("[TONTERRY] Application version {}", properties.getVersion());
  }

}
