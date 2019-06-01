package com.e3k.fountain.webcontrol;

import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.*;

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

    public static SwitchableDevice deviceByType(DeviceType deviceType) {
        if (deviceType == DeviceType.fountain) return FountainDevice.ONE;
        if (deviceType == DeviceType.light) return LightDevice.ONE;
        if (deviceType == DeviceType.auxGpio1) return AuxGpio1Device.ONE;
        if (deviceType == DeviceType.auxGpio2) return AuxGpio2Device.ONE;
        if (deviceType == DeviceType.auxGpio3) return AuxGpio3Device.ONE;
        if (deviceType == DeviceType.auxGpio4) return AuxGpio4Device.ONE;
        if (deviceType == DeviceType.auxGpio5) return AuxGpio5Device.ONE;
        if (deviceType == DeviceType.auxGpio6) return AuxGpio6Device.ONE;
        throw new IllegalArgumentException("Unknown device type " + deviceType);
    }
}
