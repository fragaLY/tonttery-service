package by.vk.tonttery.api.lottery;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

public enum Type {

  DAILY, WEEKLY, MONTHLY, YEARLY;

  public static Set<Type> typesForNow() {
    var now = LocalDate.now();
    var firstDayOfYear = now.equals(now.with(TemporalAdjusters.firstDayOfYear()));
    var firstDayOfMonth = now.equals(now.with(TemporalAdjusters.firstDayOfMonth()));
    var firstDayOfWeek = now.equals(now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
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
}
