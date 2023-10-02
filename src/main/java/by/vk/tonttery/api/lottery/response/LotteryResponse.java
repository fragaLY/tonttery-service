/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api.lottery.response;

import by.vk.tonttery.api.client.repository.Client;
import by.vk.tonttery.api.lottery.repository.Lottery;
import by.vk.tonttery.api.lottery.repository.Status;
import by.vk.tonttery.api.lottery.repository.Type;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The full definition of the lottery. It is used to return the data of lottery to the client.
 *
 * @param id                     - the id of lottery. Should not be null.
 * @param winnerId               - the winner's id. Same to client's id. Should not be null.
 * @param winnerTelegramUserName - the winner's telegram username. Should not be blank.
 * @param type                   - the type of lottery. Should not be null.
 * @param status                 - the status of lottery. Should not be null.
 * @param startDate              - the start date of lottery. Should not be null.
 * @param players                - the players count of lottery. Should be positive or zero.
 * @param prize                  - the prize of lottery. Should be positive or zero.
 * @param joined                 - the joined flag for the client for the lottery. Should not be
 *                               null.
 */
public record LotteryResponse(@NotNull(message = "The id of lottery should not be null") UUID id,
                              @Nullable UUID winnerId,
                              @Nullable String winnerTelegramUserName,
                              @NotNull(message = "The type of lottery should not be null") Type type,
                              @NotNull(message = "The status of lottery should not be null") Status status,
                              @NotNull LocalDate startDate,
                              @PositiveOrZero(message = "The minimal amount of players should be zero") int players,
                              @PositiveOrZero(message = "The minimal prize should be zero") BigDecimal prize,
                              @Nullable Boolean joined) {

  /**
   * The convertor from the entity of lottery to the response of lottery.
   *
   * @param entity - the entity of lottery. Should not be null.
   * @param winner - the winner's entity. Should not be null.
   * @param prize  - the prize of lottery. Should be positive or zero.
   * @param joined - the joined flag for the client for the lottery. Should not be null.
   *
   * @return the response of lottery. Should not be null.
   */
  @NotNull(message = "The lottery response should not be null")
  public static LotteryResponse from(
      @NotNull(message = "The entity of lottery should not be null") Lottery entity,
      @Nullable Client winner,
      @PositiveOrZero(message = "The minimal prize should be zero") BigDecimal prize,
      @Nullable Boolean joined) {
    return new LotteryResponse(entity.getId(), winner == null ? null : winner.getId().getId(),
        winner == null ? null : winner.getTelegramUserName(),
        entity.getType(), entity.getStatus(), entity.getStartDate(), entity.getClients().size(),
        prize,
        joined);
  }

  /**
   * The convertor from the entity of lottery to the response of lottery.
   *
   * @param entity - the entity of lottery. Should not be null.
   * @param winner - the winner's entity. Should not be null.
   * @param prize  - the prize of lottery. Should be positive or zero.
   *
   * @return the lottery's response. Should not be null.
   */
  @NotNull(message = "The lottery response should not be null")
  public static LotteryResponse from(
      @NotNull(message = "The entity of lottery should not be null") Lottery entity,
      @Nullable Client winner,
      @PositiveOrZero(message = "The minimal prize should be zero") BigDecimal prize) {
    return new LotteryResponse(entity.getId(), winner == null ? null : winner.getId().getId(),
        winner == null ? null : winner.getTelegramUserName(),
        entity.getType(), entity.getStatus(), entity.getStartDate(), entity.getClients().size(),
        prize,
        false);
  }


  /**
   * The convertor from the entity of lottery to the response of lottery.
   *
   * @param entity - the entity of lottery. Should not be null.
   * @param prize  - the prize of lottery. Should be positive or zero.
   *
   * @return the lottery's response. Should not be null.
   */
  @NotNull(message = "The lottery response should not be null")
  public static LotteryResponse from(
      @NotNull(message = "The entity of lottery should not be null") Lottery entity,
      @PositiveOrZero(message = "The minimal prize should be zero") BigDecimal prize) {
    return new LotteryResponse(entity.getId(), null, null,
        entity.getType(), entity.getStatus(), entity.getStartDate(), entity.getClients().size(),
        prize,
        false);
  }
}
