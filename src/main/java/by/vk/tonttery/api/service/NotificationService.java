package by.vk.tonttery.api.service;

import by.vk.tonttery.api.lottery.response.LotteryResponse;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public record Notifier() {

  public void channel(LotteryResponse lottery) {
    switch (lottery.status()) {
      case CREATED -> log.info(
          "The winner of [{}] lottery of [{}] among the [{}] players is [{}]. The prize is [{}] TON.",
          lottery.type().name().toLowerCase(), lottery.startDate(), lottery.players(),
          lottery.winnerTelegramUserName(), lottery.prize());
      case COMPLETED -> log.info(
          "Created a new lottery [{}]. The winner will be announced [{}] at midnight.",
          lottery.type().name().toLowerCase(), lottery.startDate());
    }
  }

  public void channel(List<LotteryResponse> lotteries) {
    var prize = lotteries.stream().map(LotteryResponse::prize)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    log.info(
        "The upcoming lotteries are sharing the prize pool of [{}].",
        prize);
  }

  public void person(LotteryResponse lottery) {
    log.info(
        "Congratulation [{}], You are the winner of [{}] lottery of [{}] among the [{}] players. You prize of [{}] TON have been sent.",
        lottery.winnerTelegramUserName(), lottery.type().name().toLowerCase(), lottery.startDate(),
        lottery.players(), lottery.prize());
  }

}
