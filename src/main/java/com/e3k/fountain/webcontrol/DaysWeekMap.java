package com.e3k.fountain.webcontrol;

import java.time.DayOfWeek;
import java.util.EnumMap;

public class DaysWeekMap<T> extends EnumMap<DayOfWeek, T> {

    public DaysWeekMap() {
        super(DayOfWeek.class);
        put(DayOfWeek.MONDAY, null);
        put(DayOfWeek.TUESDAY, null);
        put(DayOfWeek.WEDNESDAY, null);
        put(DayOfWeek.THURSDAY, null);
        put(DayOfWeek.FRIDAY, null);
        put(DayOfWeek.SATURDAY, null);
        put(DayOfWeek.SUNDAY, null);
    }

}
