package com.e3k.fountain.webcontrol;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public class Utils {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String timeToString(LocalTime time) {
        if (time == null) {
            return null;
        }
        return dateTimeFormatter.format(time);
    }

    public static LocalTime stringToTime(String str) {
        if (str == null) {
            return null;
        }
        return LocalTime.parse(str, dateTimeFormatter);
    }
}
