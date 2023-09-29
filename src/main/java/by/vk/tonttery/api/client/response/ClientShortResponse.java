package by.vk.tonttery.api.client.response;

import by.vk.tonttery.api.client.repository.Client;
import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponse(UUID id, String name, Long telegramId, String telegramUserName,
                             String image, LocalDateTime authenticatedAt) {

  public static ClientResponse from(Client entity) {
    return new ClientResponse(entity.getId().getId(),
        entity.getFirstName() + " " + entity.getLastName(),
        entity.getId().getTelegramId(), entity.getTelegramUserName(), entity.getImage(),
        entity.getAuthenticatedAt());
  }

}
