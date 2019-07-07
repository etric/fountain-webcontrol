package com.e3k.fountain.webcontrol.config;


import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
class DeviceConfig {

    @Setter(AccessLevel.PACKAGE)
    private DeviceState state = DeviceState.off;

    private int pin = -1;
    private String label;
    private DaysWeekMap<LocalTime> alarmsStart;
    private DaysWeekMap<LocalTime> alarmsEnd;

    DeviceConfig(DeviceType deviceType) {
        this.label = deviceType.name();
        this.alarmsStart = null;
        this.alarmsEnd = null;
    }

    DeviceConfig(DeviceType deviceType, DaysWeekMap<LocalTime> alarmsStart, DaysWeekMap<LocalTime> alarmsEnd) {
        this.label = deviceType.name();
        this.alarmsStart = alarmsStart;
        this.alarmsEnd = alarmsEnd;
    }
}
