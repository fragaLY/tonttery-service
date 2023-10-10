/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonterry.configuration.lottery;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.vk.tonterry.TestObjects;
import by.vk.tonttery.api.lottery.repository.Type;
import by.vk.tonttery.api.lottery.response.LotteryResponse;
import by.vk.tonttery.api.service.NotificationService;
import by.vk.tonttery.api.service.TontteryService;
import by.vk.tonttery.configuration.lottery.ChronConfiguration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Tags({@Tag("unit"), @Tag("chron")})
class ChronConfigurationTest {

  @Mock
  TontteryService service;

  @Mock
  NotificationService notifier;

  @InjectMocks
  ChronConfiguration configuration;

  @Test
  @DisplayName("Awarding winners chron event")
  void award() {
    //given
    when(service.award(any(), any())).thenReturn(TestObjects.lotteryResponse(UUID.randomUUID(),
        Arrays.stream(Type.values()).findAny().get(), LocalDate.now()));

    //when
    configuration.award();

    //then
    verify(notifier, atLeast(1)).channel(any(LotteryResponse.class));
    verify(notifier, atLeast(1)).person(any(LotteryResponse.class));
  }

  @Test
  @DisplayName("Creating a new lottery")
  void create() {
    //given
    when(service.create(any(), any())).thenReturn(TestObjects.lotteryResponse(UUID.randomUUID(),
        Arrays.stream(Type.values()).findAny().get(), LocalDate.now()));

    //when
    configuration.create();

    //then
    verify(notifier, atLeast(1)).channel(any(LotteryResponse.class));
  }

  @Test
  @DisplayName("Overview of upcoming lotteries")
  void overview() {
    //given
    when(service.overview(any())).thenReturn(List.of(TestObjects.lotteryResponse(UUID.randomUUID(),
        Arrays.stream(Type.values()).findAny().get(), LocalDate.now())));

    //when
    configuration.overview();

    //then
    verify(notifier, atLeast(1)).channel(anyList());
  }
}