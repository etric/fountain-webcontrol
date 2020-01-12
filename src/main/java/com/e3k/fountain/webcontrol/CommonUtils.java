package com.e3k.fountain.webcontrol;

import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.*;
import com.pi4j.system.SystemInfo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public class CommonUtils {

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

    public static SwitchableDevice deviceByType(DeviceType deviceType) {
        if (deviceType == DeviceType.fountain) return FountainDevice.ONE;
        if (deviceType == DeviceType.sound) return SoundDevice.ONE;
        if (deviceType == DeviceType.light) return LightDevice.ONE;
        throw new IllegalArgumentException("Unknown device type " + deviceType);
    }

    public static DaysWeekMap<LocalTime> daysWeekMap(LocalTime time) {
        DaysWeekMap<LocalTime> m = new DaysWeekMap<>();
        m.put(DayOfWeek.MONDAY, time);
        m.put(DayOfWeek.TUESDAY, time);
        m.put(DayOfWeek.WEDNESDAY, time);
        m.put(DayOfWeek.THURSDAY, time);
        m.put(DayOfWeek.FRIDAY, time);
        m.put(DayOfWeek.SATURDAY, time);
        m.put(DayOfWeek.SUNDAY, time);
        return m;
    }

    public static boolean isAlarmable(DeviceType type) {
        return (type == DeviceType.fountain || type == DeviceType.sound || type == DeviceType.light);
    }

    public static boolean isRaspberry() {
        return SystemInfo.getOsName().startsWith("Linux");
    }

    public static String getAppVersion() {
        String appVersion = CommonUtils.class.getPackage().getImplementationVersion();
        if (appVersion == null || appVersion.isEmpty()) {
            return "N/A";
        }
        return appVersion;
    }

    public static byte setOrUnsetBit(byte val, int bitNum, boolean setBit) {
        bitNum = bitNum % 8;
        if (!setBit) {
            return (byte) (val & ~(1 << bitNum));
        } else {
            return (byte) (val | (1 << bitNum));
        }
    }

    public static boolean isBitUp(byte val, int bitNum) {
        return (val & (1 << bitNum)) > 0;
    }
}
