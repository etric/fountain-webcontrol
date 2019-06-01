package com.e3k.fountain.webcontrol;

import spark.utils.StringUtils;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public String serialize(Function<T, String> toStringFn) {
        return values().stream()
                .map(val -> (val == null) ? "null" : toStringFn.apply(val))
                .collect(Collectors.joining(","));
    }

    public static <T> DaysWeekMap<T> deserialize(Function<String, T> fromStringFn, String rawString) {
        Objects.requireNonNull(fromStringFn);
        Objects.requireNonNull(rawString);

        List<T> parsed = Arrays.stream(rawString.split(","))
                .map(strVal -> StringUtils.isBlank(strVal) || strVal.equalsIgnoreCase("null")
                        ? null : fromStringFn.apply(strVal))
                .collect(Collectors.toList());

        if (parsed.size() != 7) {
            throw new IllegalArgumentException("Cannot deserialize " + rawString + ": size must be 7!");
        }
        DaysWeekMap<T> result = new DaysWeekMap<>();
        result.put(DayOfWeek.MONDAY, parsed.get(0));
        result.put(DayOfWeek.TUESDAY, parsed.get(1));
        result.put(DayOfWeek.WEDNESDAY, parsed.get(2));
        result.put(DayOfWeek.THURSDAY, parsed.get(3));
        result.put(DayOfWeek.FRIDAY, parsed.get(4));
        result.put(DayOfWeek.SATURDAY, parsed.get(5));
        result.put(DayOfWeek.SUNDAY, parsed.get(6));
        return result;
    }
}
