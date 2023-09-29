package by.vk.tonttery.api.client.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The client's composite primary key.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientId implements Serializable {

  @Column(name = "id", updatable = false)
  private UUID id;

  @Column(name = "telegram_id", updatable = false)
  private Long telegramId;
}
