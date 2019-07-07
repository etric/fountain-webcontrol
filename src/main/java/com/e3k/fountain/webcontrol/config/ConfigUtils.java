package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.google.gson.*;
import lombok.experimental.UtilityClass;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;

@UtilityClass
class ConfigUtils {

    static Gson buildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>)
                        (localTime, t, c) -> new JsonPrimitive(Utils.timeToString(localTime)))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>)
                        (jsonEl, t, c) -> Utils.stringToTime(jsonEl.getAsString()))
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
        } else {
            throw new IllegalArgumentException("Unsupported alarm type " + alarmType);
        }
    }

    static void requireInRange(int target, int min, int max, String msg) {
        if (target < min || target > max) {
            throw new IllegalArgumentException(msg + ": " + target +
                    " is not in range [" + min + ".." + max + "]");
        }
    }

    static void requireState(boolean state, String msg) {
        if (!state) {
            throw new IllegalArgumentException(msg);
        }
    }

    static void requireAddress(String address, String msg) {
        Objects.requireNonNull(address, msg);
        try {
            new InternetAddress(address);
        } catch (AddressException e) {
            throw new IllegalArgumentException(msg + ": " + address + " has invalid address");
        }
    }

    static byte setOrUnsetBit(byte val, int bitNum, boolean setBit) {
        bitNum = bitNum % 8;
        if (!setBit) {
            return (byte) (val & ~(1 << bitNum));
        } else {
            return (byte) (val | (1 << bitNum));
        }
    }
}
