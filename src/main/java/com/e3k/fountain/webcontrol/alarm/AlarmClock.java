package com.e3k.fountain.webcontrol.alarm;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.alarm.handler.EndAlarmListener;
import com.e3k.fountain.webcontrol.alarm.handler.StartAlarmListener;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.*;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmManager;
import fr.dyade.jdring.PastDateException;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    private final Map<AlarmType, DaysWeekMap<AlarmEntry>> alarms = initializedAlarmsMap();
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
                for (Map.Entry<AlarmType, DaysWeekMap<AlarmEntry>> entry : alarms.entrySet()) {
                    final AlarmType alarmType = entry.getKey();
                    final DaysWeekMap<AlarmEntry> daysWeekMap = entry.getValue();
                    for (Map.Entry<DayOfWeek, AlarmEntry> entry2 : daysWeekMap.entrySet()) {
                        final DayOfWeek dayOfWeek = entry2.getKey();
                        log.debug("Adding Alarm {}/{}", alarmType, dayOfWeek);
                        AlarmEntry alarmEntry = entry2.getValue();
                        alarmManager.addAlarm(alarmEntry);
                    }
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

    public synchronized void updateTime(DayOfWeek dayOfWeek, AlarmType alarmType, LocalTime time) {
        if (alarmManager != null) {
            log.info("Updating Alarm {}/{}: {}", alarmType, dayOfWeek, time);

            final AlarmEntry alarm = alarms.get(alarmType).get(dayOfWeek);
            if (alarm.minute == time.getMinute() && alarm.hour == time.getHour()) {
                log.debug("Alarm {}/{} is not updated as time isn't changed", alarmType, dayOfWeek);
                return;
            }

            final SwitchableDevice device = getAffectedDeviceByAlarmType(alarmType);
            final boolean oldIsAlarmActive = isAlarmActive(dayOfWeek, device.getType());

            alarmManager.removeAlarm(alarm);
            alarm.minute = time.getMinute();
            alarm.hour = time.getHour();
            alarm.updateAlarmTime();

            if (dayOfWeek == LocalDate.now().getDayOfWeek()) {
                final boolean newIsAlarmActive = isAlarmActive(dayOfWeek, device.getType());
                if (oldIsAlarmActive != newIsAlarmActive) {
                    log.debug("Alarm update caused state change => switching device");
                    device.switchState(DeviceState.fromBool(newIsAlarmActive));
                }
            }

            PropertiesManager.ONE.setAlarmClock(alarmType, dayOfWeek, time);
            try {
                alarmManager.addAlarm(alarm);
            } catch (PastDateException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public DaysWeekMap<String> getWeekAlarms(AlarmType alarmType) {
        log.debug("Getting alarm {} value", alarmType);
        final DaysWeekMap<AlarmEntry> daysWeekMapAlarms = alarms.get(alarmType);
        final DaysWeekMap<String> result = new DaysWeekMap<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            AlarmEntry alarm = daysWeekMapAlarms.get(dayOfWeek);
            result.put(dayOfWeek, alarmToTimeString(alarm));
        }
        return result;
    }

    private static void putAlarmStartMapping(Map<AlarmType, DaysWeekMap<AlarmEntry>> map, AlarmType alarmType, SwitchableDevice switchableDevice) {
        map.put(alarmType, buildWeekAlarmEntries(9, 0, new StartAlarmListener(switchableDevice), alarmType));
    }

    private static void putAlarmEndMapping(Map<AlarmType, DaysWeekMap<AlarmEntry>> map, AlarmType alarmType, SwitchableDevice switchableDevice) {
        map.put(alarmType, buildWeekAlarmEntries(18, 0, new EndAlarmListener(switchableDevice), alarmType));
    }

    private Map<AlarmType, DaysWeekMap<AlarmEntry>> initializedAlarmsMap() {
        Map<AlarmType, DaysWeekMap<AlarmEntry>> map = new HashMap<>();

        putAlarmStartMapping(map, fountainAlarmStart, FountainDevice.ONE);
        putAlarmEndMapping(map, fountainAlarmEnd, FountainDevice.ONE);

        putAlarmStartMapping(map, lightAlarmStart, LightDevice.ONE);
        putAlarmEndMapping(map, lightAlarmEnd, LightDevice.ONE);

        putAlarmStartMapping(map, soundAlarmStart, SoundDevice.ONE);
        putAlarmEndMapping(map, soundAlarmEnd, SoundDevice.ONE);

        putAlarmStartMapping(map, auxGpio1AlarmStart, AuxGpio1Device.ONE);
        putAlarmEndMapping(map, auxGpio1AlarmEnd, AuxGpio1Device.ONE);

        putAlarmStartMapping(map, auxGpio2AlarmStart, AuxGpio2Device.ONE);
        putAlarmEndMapping(map, auxGpio2AlarmEnd, AuxGpio2Device.ONE);

        putAlarmStartMapping(map, auxGpio3AlarmStart, AuxGpio3Device.ONE);
        putAlarmEndMapping(map, auxGpio3AlarmEnd, AuxGpio3Device.ONE);

        putAlarmStartMapping(map, auxGpio4AlarmStart, AuxGpio4Device.ONE);
        putAlarmEndMapping(map, auxGpio4AlarmEnd, AuxGpio4Device.ONE);

        putAlarmStartMapping(map, auxGpio5AlarmStart, AuxGpio5Device.ONE);
        putAlarmEndMapping(map, auxGpio5AlarmEnd, AuxGpio5Device.ONE);

        putAlarmStartMapping(map, auxGpio6AlarmStart, AuxGpio6Device.ONE);
        putAlarmEndMapping(map, auxGpio6AlarmEnd, AuxGpio6Device.ONE);

        return Collections.unmodifiableMap(map);
    }

    private void reSyncDeviceStateWithAlarms(SwitchableDevice device) {
        DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();
        DeviceState alarmDeviceState = DeviceState.fromBool(isAlarmActive(todayDayOfWeek, device.getType()));
        if (device.currentState() != alarmDeviceState) {
            log.info("Re-syncing device state due to Alarm state");
            device.switchState(alarmDeviceState);
        }
    }

    private boolean isAlarmActive(DayOfWeek dayOfWeek, DeviceType deviceType) {
        final AlarmEntry alarmStart;
        final AlarmEntry alarmEnd;
        if (deviceType == DeviceType.fountain) {
            alarmStart = alarms.get(fountainAlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(fountainAlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.light) {
            alarmStart = alarms.get(lightAlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(lightAlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.sound) {
            alarmStart = alarms.get(soundAlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(soundAlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.auxGpio1) {
            alarmStart = alarms.get(auxGpio1AlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(auxGpio1AlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.auxGpio2) {
            alarmStart = alarms.get(auxGpio2AlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(auxGpio2AlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.auxGpio3) {
            alarmStart = alarms.get(auxGpio3AlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(auxGpio3AlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.auxGpio4) {
            alarmStart = alarms.get(auxGpio4AlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(auxGpio4AlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.auxGpio5) {
            alarmStart = alarms.get(auxGpio5AlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(auxGpio5AlarmEnd).get(dayOfWeek);
        } else if (deviceType == DeviceType.auxGpio6) {
            alarmStart = alarms.get(auxGpio6AlarmStart).get(dayOfWeek);
            alarmEnd = alarms.get(auxGpio6AlarmEnd).get(dayOfWeek);
        } else {
            throw new IllegalArgumentException();
        }
        return isNowWithinPeriod(alarmStart, alarmEnd);
    }
}
