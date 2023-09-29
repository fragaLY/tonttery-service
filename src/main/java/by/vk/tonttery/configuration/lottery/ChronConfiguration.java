package by.vk.tonttery.configuration.lottery;

import by.vk.tonttery.api.lottery.repository.Type;
import by.vk.tonttery.api.service.NotificationService;
import by.vk.tonttery.api.service.TonterryService;
import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class LotteryConfiguration implements AsyncConfigurer {

  private final TonterryService service;
  private final NotificationService notifier;

  @Async
  @Scheduled(cron = "@midnight")
  @Transactional(noRollbackFor = Exception.class)
  // todo vk: no roll back for notification exceptions
  public void award() {
    log.info("[TONTTERY] Lottery awarding process is started");
    var startDate = LocalDate.now(Clock.systemUTC());
    Type.typesForDate(startDate)
        .parallelStream()
        .map(it -> service.award(it, startDate))
        .forEach(it -> {
          notifier.channel(it);
          notifier.person(it);
        });
  }

  @Async
  @Scheduled(cron = "@midnight")
  @Transactional(noRollbackFor = Exception.class)
  // todo vk: no roll back for notification exceptions
  public void create() {
    log.info("[TONTTERY] Lottery creation process is started");
    var now = LocalDate.now(Clock.systemUTC());
    Type.typesForDate(now)
        .parallelStream()
        .map(it -> service.create(it, now))
        .forEach(notifier::channel);
  }

  @Async
  @Scheduled(cron = "0 0 18 * * * *")
  @Transactional(noRollbackFor = Exception.class)
  // todo vk: no roll back for notification exceptions
  public void overview() {
    log.info("[TONTTERY] Lotteries overview process is started");
    var now = LocalDate.now(Clock.systemUTC());
    notifier.channel(service.overview(now));
  }

  @Bean(destroyMethod = "shutdown")
  @Override
  public ExecutorService getAsyncExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }
}
