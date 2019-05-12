package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.pi4j.system.SystemInfo;
import org.slf4j.Logger;

public interface SwitchableDevice {

    default DeviceState currentState() {
        if (SystemInfo.getOsName().startsWith("Linux")) { //dirty check if running on Raspberry
            return PinJokey.ONE.getDevicePinState(getType());
        }
        return PropertiesManager.ONE.getDeviceManualState(getType()); // dirty fallback check
    }

    default void switchState(DeviceState deviceState) {
        getLogger().info("Switching {} {}", getType(), deviceState);
        PinJokey.ONE.setDevicePinState(getType(), deviceState);
    }

    default void restoreState() {
        DeviceState manualState = PropertiesManager.ONE.getDeviceManualState(getType());
        if (currentState() != manualState) {
            getLogger().info("Restoring {} state", getType());
            switchState(manualState);
        }
    }

    DeviceType getType();

    Logger getLogger();
}
