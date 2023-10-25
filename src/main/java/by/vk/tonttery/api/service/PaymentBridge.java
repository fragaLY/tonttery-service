/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api.service;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The payments bridge.
 */
@Slf4j
@Service
public record PaymentBridge() {

  public boolean send(BigDecimal coins, String to) {
    log.info("[TONTTERY] The coins {} were sent to {}.", coins, to);
    return true;
  }

  public boolean receive(BigDecimal coins, String from) {
    log.info("[TONTTERY] The coins {} were received from {}.", coins, from);
    return true;
  }
}
