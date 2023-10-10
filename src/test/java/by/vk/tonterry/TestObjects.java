/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonterry;

import by.vk.tonttery.api.client.repository.Client;
import by.vk.tonttery.api.client.repository.ClientId;
import by.vk.tonttery.api.lottery.repository.Lottery;
import by.vk.tonttery.api.lottery.repository.Status;
import by.vk.tonttery.api.lottery.repository.Type;
import by.vk.tonttery.api.lottery.response.LotteryResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestObjects {

  public static Lottery lottery(UUID lotteryId, Type type, LocalDate startDate) {
    var createdAt = LocalDateTime.now();
    return new Lottery(lotteryId, null, type, Status.CREATED, startDate, null, createdAt,
        createdAt);
  }

  public static LotteryResponse lotteryResponse(UUID lotteryId, Type type, LocalDate startDate) {
    return new LotteryResponse(lotteryId, null, null, type, Status.CREATED,
        startDate, 0, BigDecimal.ZERO, false);
  }

  public static Client client(UUID clientInternalId, Long telegramId, String userName) {
    return new Client(new ClientId(clientInternalId, telegramId), "Vadzim", "Kavalkou", userName,
        false, true,
        "shortenUrl", LocalDateTime.now(), null, LocalDateTime.now(), LocalDateTime.now());
  }

}
