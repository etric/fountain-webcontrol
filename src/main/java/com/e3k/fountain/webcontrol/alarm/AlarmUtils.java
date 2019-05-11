package com.e3k.fountain.webcontrol.alarm;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.io.FountainDevice;
import com.e3k.fountain.webcontrol.io.LightDevice;
import com.e3k.fountain.webcontrol.io.SoundDevice;
import com.e3k.fountain.webcontrol.io.SwitchableDevice;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;
import fr.dyade.jdring.PastDateException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

import static com.e3k.fountain.webcontrol.constant.AlarmType.*;

@Slf4j
final class AlarmUtils {

    private AlarmUtils() {
        throw new AssertionError();
    }

    static SwitchableDevice getDeviceByAlarmType(AlarmType alarmType) {
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

    static AlarmEntry buildAlarmEntry(int defaultHour, int defaultMinute,
                                      AlarmListener alarmListener, AlarmType alarmType) {
        int hour = defaultHour;
        int minute = defaultMinute;
        LocalTime alarmTime = PropertiesManager.ONE.getAlarmClock(alarmType);
        if (alarmTime != null) {
            hour = alarmTime.get(ChronoField.HOUR_OF_DAY);
            minute = alarmTime.get(ChronoField.MINUTE_OF_HOUR);
        }
        return newAlarmEntry(hour, minute, alarmListener);
    }

    private static AlarmEntry newAlarmEntry(int hour, int min, AlarmListener listener) {
        try {
            return new AlarmEntry(min, hour, -1, -1, -1, -1, listener);
        } catch (PastDateException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
