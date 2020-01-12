package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.CommonUtils;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.google.gson.*;
import lombok.experimental.UtilityClass;

import java.time.LocalTime;
import java.util.Map;

@UtilityClass
public class ConfigUtils {

    static Gson buildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>)
                        (localTime, t, c) -> new JsonPrimitive(CommonUtils.timeToString(localTime)))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>)
                        (jsonEl, t, c) -> CommonUtils.stringToTime(jsonEl.getAsString()))
                .create();
    }

    static DaysWeekMap<LocalTime> alarmsConfigByAlarmType(Map<DeviceType, DeviceConfig> devices, AlarmType alarmType) {
        if (alarmType == AlarmType.fountainAlarmStart) {
            return devices.get(DeviceType.fountain).getAlarmsStart();
        } else if (alarmType == AlarmType.fountainAlarmEnd) {
            return devices.get(DeviceType.fountain).getAlarmsEnd();
        } else if (alarmType == AlarmType.lightAlarmStart) {
            return devices.get(DeviceType.light).getAlarmsStart();
        } else if (alarmType == AlarmType.lightAlarmEnd) {
            return devices.get(DeviceType.light).getAlarmsEnd();
        } else if (alarmType == AlarmType.soundAlarmStart) {
            return devices.get(DeviceType.sound).getAlarmsStart();
        } else if (alarmType == AlarmType.soundAlarmEnd) {
            return devices.get(DeviceType.sound).getAlarmsEnd();
        } else {
            throw new IllegalArgumentException("Unsupported alarm type " + alarmType);
        }
    }
}
