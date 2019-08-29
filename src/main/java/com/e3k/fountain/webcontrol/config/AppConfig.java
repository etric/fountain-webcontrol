package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Map;
import java.util.stream.Stream;

import static com.e3k.fountain.webcontrol.Utils.daysWeekMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Getter
class AppConfig {

    private static final LocalTime DEFAULT_ALARM_START = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_ALARM_END = LocalTime.of(18, 0);

    @Setter(AccessLevel.PACKAGE)
    private ControlMode controlMode = ControlMode.manual;

    @Setter(AccessLevel.PACKAGE)
    private int pauseBetweenTracks = 0;

    @Setter(AccessLevel.PACKAGE)
    private int lastPlayedItem = -1;

    @Setter(AccessLevel.PACKAGE)
    private int volume = 50;

    private String object = "Подвал";
    private int blinkDelayMs = 100;
    private int httpPort = 80;
    private boolean playFromStartOnModeSwitch = false;
    private boolean soundDevicesEnabled = true;

    private UmfConfig umf = new UmfConfig();
    private Map<DeviceType, DeviceConfig> devices = defaultDevicesConfig();

    private static Map<DeviceType, DeviceConfig> defaultDevicesConfig() {
        return Stream.of(DeviceType.values()).collect(toMap(identity(), type ->
            Utils.isAlarmable(type)
                    ? new DeviceConfig(type, daysWeekMap(DEFAULT_ALARM_START), daysWeekMap(DEFAULT_ALARM_END))
                    : new DeviceConfig(type)
        ));
    }
}
