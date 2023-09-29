/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api.lottery.response;

import by.vk.tonttery.api.lottery.repository.Lottery;
import by.vk.tonttery.api.lottery.repository.Status;
import by.vk.tonttery.api.lottery.repository.Type;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The short definition of the lottery. It is used to return the lottery's data to the client.
 *
 * @param id        - the id of lottery. Should not be null.
 * @param type
 * @param status
 * @param startDate
 */
public record LotteryShortResponse(
    @NotNull(message = "The id of lottery should not be null") UUID id,
    @NotNull(message = "The type of lottery should not be null") Type type,
    @NotNull(message = "The status of lottery should not be null") Status status,
    @NotNull LocalDate startDate) {


  /**
   * The convertor from the entity of lottery to the response of lottery.
   *
   * @param entity - the entity of lottery. Should not be null.
   *
   * @return the response of lottery. Should not be null.
   */
  @NotNull(message = "The lottery response should not be null")
  public static LotteryShortResponse from(
      @NotNull(message = "The entity of lottery should not be null") Lottery entity) {
    return new LotteryShortResponse(entity.getId(),
        entity.getType(), entity.getStatus(), entity.getStartDate());
  }

}
