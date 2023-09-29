package by.vk.tonttery.api.lottery.response;

import by.vk.tonttery.api.client.repository.Client;
import by.vk.tonttery.api.lottery.repository.Lottery;
import by.vk.tonttery.api.lottery.repository.Status;
import by.vk.tonttery.api.lottery.repository.Type;
import java.time.LocalDate;
import java.util.UUID;

public record LotteryResponse(UUID id, UUID winnerId, String winnerTelegramUserName, Type type,
                              Status status, LocalDate startDate) {

  public static LotteryResponse from(Lottery entity, Client winner) {
    return new LotteryResponse(entity.getId(), winner.getId().getId(), winner.getTelegramUserName(),
        entity.getType(), entity.getStatus(), entity.getStartDate());
  }
}
