package by.vk.tonttery.api.client.response;

import by.vk.tonttery.api.client.repository.Client;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * The short definition of the client. It is used to return the client's data to the client.
 *
 * @param id               - the client's id. Should not be null.
 * @param name             - the client's name. Should not be blank.
 * @param telegramUserName - the client's telegram username. Should not be blank.
 */
public record ClientShortResponse(@NotNull(message = "The client's id should not be null") UUID id,
                                  @NotBlank(message = "The client's name should not be blank") String name,
                                  @NotBlank(message = "The client's telegram name should not be blank") String telegramUserName) {

  /**
   * The convertor from the client's entity to the client's response.
   *
   * @param entity - the client's entity. Should not be null.
   *
   * @return the client's response. Should not be null.
   */
  @NotNull(message = "The client's response should not be null")
  public static ClientShortResponse from(
      @NotNull(message = "The client's entity should not be null") Client entity) {
    return new ClientShortResponse(entity.getId().getId(),
        entity.getFirstName() + " " + entity.getLastName(),
        entity.getTelegramUserName());
  }
}
