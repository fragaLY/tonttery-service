/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

package by.vk.tonttery.api.lottery.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

/**
 * The type of lottery.
 */
public enum Type {

  DAILY, WEEKLY, MONTHLY, YEARLY;

  private static final Long ONE = 1L;

  /**
   * Searches types of lottery for the date.
   *
   * @param date - the date for searching types of lottery.
   *
   * @return the set of types for the date.
   */
  public static Set<Type> typesForDate(LocalDate date) {
    var firstDayOfYear = date.equals(date.with(TemporalAdjusters.firstDayOfYear()));
    var firstDayOfMonth = date.equals(date.with(TemporalAdjusters.firstDayOfMonth()));
    var firstDayOfWeek = date.equals(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
    var types = new HashSet<Type>(Type.values().length);
    types.add(DAILY);

    if (firstDayOfYear) {
      types.add(YEARLY);
    }

    if (firstDayOfMonth) {
      types.add(MONTHLY);
    }

    if (firstDayOfWeek) {
      types.add(WEEKLY);
    }

    return types;
  }

  /**
   * Searches next lottery date for the provided date.
   *
   * @param now - the date for searching next lottery date.
   *
   * @return the next lottery date.
   */
  public LocalDate nextLotteryDate(LocalDate now) {
    return switch (this) {
      case DAILY -> now.plusDays(ONE);
      case WEEKLY -> now.plusWeeks(ONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      case MONTHLY -> now.plusMonths(ONE).with(TemporalAdjusters.firstDayOfMonth());
      case YEARLY -> now.plusYears(ONE).with(TemporalAdjusters.firstDayOfYear());
    };
  }
}
