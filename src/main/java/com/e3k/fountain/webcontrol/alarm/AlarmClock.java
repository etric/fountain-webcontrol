package com.e3k.fountain.webcontrol.alarm;

import com.e3k.fountain.webcontrol.alarm.handler.*;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.FountainDevice;
import com.e3k.fountain.webcontrol.io.LightDevice;
import com.e3k.fountain.webcontrol.io.SoundDevice;
import com.e3k.fountain.webcontrol.io.SwitchableDevice;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmManager;
import fr.dyade.jdring.PastDateException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.e3k.fountain.webcontrol.alarm.AlarmUtils.*;
import static com.e3k.fountain.webcontrol.constant.AlarmType.*;

/**
 * @author Alexander 'etric' Khamylov
 */
@Slf4j
public enum AlarmClock {

    ONE;

    private final Map<AlarmType, AlarmEntry> alarms = initializedAlarmsMap();
    private volatile AlarmManager alarmManager;

    public synchronized void turnOn() {
        if (alarmManager == null) {
            log.info("Turning Alarms ON");
            reSyncDeviceStateWithAlarms(FountainDevice.ONE);
            reSyncDeviceStateWithAlarms(SoundDevice.ONE);
            reSyncDeviceStateWithAlarms(LightDevice.ONE);
            //TODO maybe do not create AlarmManager each time,
            // but keep it running with single dummy Alarm ?
            alarmManager = new AlarmManager();
            try {
                for (Map.Entry<AlarmType, AlarmEntry> entry : alarms.entrySet()) {
                    log.debug("Adding Alarm {}", entry.getKey());
                    alarmManager.addAlarm(entry.getValue());
                }
            } catch (PastDateException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public synchronized void turnOff() {
        if (alarmManager != null) {
            log.info("Turning Alarms OFF");
            alarmManager.removeAllAlarms();
            alarmManager = null;
        }
        LightDevice.ONE.restoreState();
        SoundDevice.ONE.restoreState();
        FountainDevice.ONE.restoreState();
    }

    public synchronized void updateTime(AlarmType alarmType, LocalTime time) {
        if (alarmManager != null) {
            log.info("Updating Alarm {}: {}", alarmType, time);

            final AlarmEntry alarm = alarms.get(alarmType);
            if (alarm.minute == time.getMinute() && alarm.hour == time.getHour()) {
                log.debug("Alarm {} is not updated as time isn't changed", alarmType);
                return;
            }

            final SwitchableDevice device = getAffectedDeviceByAlarmType(alarmType);
            final boolean oldIsAlarmActive = isAlarmActive(device.getType());

            alarmManager.removeAlarm(alarm);
            alarm.minute = time.getMinute();
            alarm.hour = time.getHour();
            alarm.updateAlarmTime();

            final boolean newIsAlarmActive = isAlarmActive(device.getType());
            if (oldIsAlarmActive != newIsAlarmActive) {
                log.debug("Alarm update caused state change => switching device");
                device.switchState(DeviceState.fromBool(newIsAlarmActive));
            }

            PropertiesManager.ONE.setAlarmClock(alarmType, time);
            try {
                alarmManager.addAlarm(alarm);
            } catch (PastDateException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public LocalTime getTime(AlarmType alarmType) {
        log.debug("Getting alarm {} value", alarmType);
        final AlarmEntry alarm = alarms.get(alarmType);
        return LocalTime.of(alarm.hour, alarm.minute);
    }

    private Map<AlarmType, AlarmEntry> initializedAlarmsMap() {
        Map<AlarmType, AlarmEntry> map = new HashMap<>();

        map.put(fountainAlarmStart, buildAlarmEntry(9, 0, new FountainStartAlarmListener(), fountainAlarmStart));
        map.put(fountainAlarmEnd, buildAlarmEntry(18, 0, new FountainEndAlarmListener(), fountainAlarmEnd));
        map.put(lightAlarmStart, buildAlarmEntry(9, 0, new LightStartAlarmListener(), lightAlarmStart));
        map.put(lightAlarmEnd, buildAlarmEntry(18, 0, new LightEndAlarmListener(), lightAlarmEnd));
        map.put(soundAlarmStart, buildAlarmEntry(9, 0, new SoundStartAlarmListener(), soundAlarmStart));
        map.put(soundAlarmEnd, buildAlarmEntry(18, 0, new SoundEndAlarmListener(), soundAlarmEnd));

        return Collections.unmodifiableMap(map);
    }

    private void reSyncDeviceStateWithAlarms(SwitchableDevice device) {
        DeviceState alarmDeviceState = DeviceState.fromBool(isAlarmActive(device.getType()));
        if (device.currentState() != alarmDeviceState) {
            log.info("Re-syncing device state due to Alarm state");
            device.switchState(alarmDeviceState);
        }
    }

    private boolean isAlarmActive(DeviceType deviceType) {
        final AlarmEntry alarmStart;
        final AlarmEntry alarmEnd;
        if (deviceType == DeviceType.fountain) {
            alarmStart = alarms.get(fountainAlarmStart);
            alarmEnd = alarms.get(fountainAlarmEnd);
        } else if (deviceType == DeviceType.light) {
            alarmStart = alarms.get(lightAlarmStart);
            alarmEnd = alarms.get(lightAlarmEnd);
        } else if (deviceType == DeviceType.sound) {
            alarmStart = alarms.get(soundAlarmStart);
            alarmEnd = alarms.get(soundAlarmEnd);
        } else {
            throw new IllegalArgumentException();
        }
        return isNowWithinPeriod(alarmStart, alarmEnd);
    }
}