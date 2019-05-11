package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.constant.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public enum LightDevice implements SwitchableDevice {
    ONE;

    @Override
    public DeviceType getType() {
        return DeviceType.light;
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
