package com.e3k.fountain.webcontrol.alarm;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.io.*;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;
import fr.dyade.jdring.PastDateException;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import static com.e3k.fountain.webcontrol.constant.AlarmType.*;

@Slf4j
final class AlarmUtils {

    private AlarmUtils() {
        throw new AssertionError();
    }

    static SwitchableDevice getAffectedDeviceByAlarmType(AlarmType alarmType) {
        if (alarmType == fountainAlarmStart || alarmType == fountainAlarmEnd) {
            return FountainDevice.ONE;
        } else if (alarmType == lightAlarmStart || alarmType == lightAlarmEnd) {
            return LightDevice.ONE;
        } else if (alarmType == soundAlarmStart || alarmType == soundAlarmEnd) {
            return SoundDevice.ONE;
        } else {
            throw new IllegalArgumentException();
        }
    }

    //TODO check performance..refactor?
    static boolean isNowWithinPeriod(AlarmEntry alarmStart, AlarmEntry alarmEnd) {
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(alarmStart.hour, alarmStart.minute);
        LocalTime endTime = LocalTime.of(alarmEnd.hour, alarmEnd.minute);
        return (!now.isBefore(startTime)) && now.isBefore(endTime);
    }

    static DaysWeekMap<AlarmEntry> buildWeekAlarmEntries(final int defaultHour, final int defaultMinute,
                                                         AlarmListener alarmListener, AlarmType alarmType) {

        DaysWeekMap<LocalTime> alarmTimeDaysWeekMap = PropertiesManager.ONE.getAlarmClocks(alarmType);
        DaysWeekMap<AlarmEntry> result = new DaysWeekMap<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            int hour = defaultHour;
            int minute = defaultMinute;
            LocalTime alarmTime = alarmTimeDaysWeekMap.get(dayOfWeek);
            if (alarmTime != null) {
                hour = alarmTime.get(ChronoField.HOUR_OF_DAY);
                minute = alarmTime.get(ChronoField.MINUTE_OF_HOUR);
            }
            result.put(dayOfWeek, newAlarmEntry(dayOfWeek, hour, minute, alarmListener));
        }
        return result;
    }

    static String alarmToTimeString(AlarmEntry alarm) {
        StringBuilder sb = new StringBuilder();
        if (alarm.hour < 10) {
            sb.append('0');
        }
        sb.append(alarm.hour);
        sb.append(':');
        if (alarm.minute < 10) {
            sb.append('0');
        }
        sb.append(alarm.minute);
        return sb.toString();
    }

    private static AlarmEntry newAlarmEntry(DayOfWeek dayOfWeek, int hour, int min, AlarmListener listener) {
        try {
            return new AlarmEntry(min, hour, -1, -1, toCalendarDayOfWeek(dayOfWeek), -1, listener);
        } catch (PastDateException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static int toCalendarDayOfWeek(DayOfWeek dayOfWeek) {
        if (DayOfWeek.MONDAY == dayOfWeek) return Calendar.MONDAY;
        if (DayOfWeek.TUESDAY == dayOfWeek) return Calendar.TUESDAY;
        if (DayOfWeek.WEDNESDAY == dayOfWeek) return Calendar.WEDNESDAY;
        if (DayOfWeek.THURSDAY == dayOfWeek) return Calendar.THURSDAY;
        if (DayOfWeek.FRIDAY == dayOfWeek) return Calendar.FRIDAY;
        if (DayOfWeek.SATURDAY == dayOfWeek) return Calendar.SATURDAY;
        if (DayOfWeek.SUNDAY == dayOfWeek) return Calendar.SUNDAY;
        throw new IllegalArgumentException("Unknown Day Of Week: " + dayOfWeek);
    }
}
