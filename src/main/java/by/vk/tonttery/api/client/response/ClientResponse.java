/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api.client.response;

import by.vk.tonttery.api.client.repository.Client;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The full definition of the client. It is used to return the client's data to the client.
 *
 * @param id               - the client's id. Should not be null.
 * @param name             - the client's name. Should not be blank.
 * @param telegramId       - the client's telegram id. Should not be null.
 * @param telegramUserName - the client's telegram username. Should not be blank.
 * @param image            - the client's image url. Can be null.
 * @param authenticatedAt  - the client's authentication date. Should be in the past or present.
 * @param updatedAt        -  client's the latest updated date time. Should be in the past or present.
 */
public record ClientResponse(@NotNull(message = "The client's id should not be null") UUID id,
                             @NotBlank(message = "The client's name should not be blank") String name,
                             @NotNull(message = "The client's telegram id should not be null") Long telegramId,
                             @NotBlank(message = "The client's telegram name should not be blank") String telegramUserName,
                             @Nullable String image,
                             @PastOrPresent(message = "The client's authentication should be in the past or present") LocalDateTime authenticatedAt,
                             @PastOrPresent(message = "The client's update date time should be in the past or present") LocalDateTime updatedAt) {

  /**
   * The convertor from the client's entity to the client's response.
   *
   * @param entity - the client's entity. Should not be null.
   * @return the client's response. Should not be null.
   */
  @NotNull(message = "The client's response should not be null")
  public static ClientResponse from(
      @NotNull(message = "The client's entity should not be null") Client entity) {
    return new ClientResponse(entity.getId().getId(),
        entity.getFirstName() + " " + entity.getLastName(), entity.getId().getTelegramId(),
        entity.getTelegramUserName(), entity.getImage(), entity.getAuthenticatedAt(),
        entity.getUpdatedAt());
  }

}
