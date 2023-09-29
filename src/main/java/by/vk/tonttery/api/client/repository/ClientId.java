/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

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
