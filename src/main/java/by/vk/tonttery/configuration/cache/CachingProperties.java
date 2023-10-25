/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.configuration.cache;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("tonttery.cache")
public record CachingProperties(
    @Min(value = 0, message = "The minimal expiration length after access should be 0 that means never to expire.")
    int expireAfterAccess,
    @Min(value = 0, message = "The minimal expiration length after write should be 0 that means never to expire.")
    int expireAfterWrite
) {

}
