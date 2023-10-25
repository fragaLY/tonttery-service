/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.configuration.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

  @Bean
  public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
    var cacheManager = new CaffeineCacheManager();
    cacheManager.setAllowNullValues(false);
    cacheManager.setCaffeine(caffeine);
    return cacheManager;
  }

  @Bean
  public Caffeine<Object, Object> caffeine(CachingProperties properties) {
    return Caffeine.newBuilder().executor(Executors.newSingleThreadExecutor())
        .expireAfterAccess(properties.expireAfterAccess(), TimeUnit.HOURS)
        .expireAfterWrite(properties.expireAfterWrite(), TimeUnit.HOURS);
  }
}
