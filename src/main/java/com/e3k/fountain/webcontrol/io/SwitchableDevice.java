package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import org.slf4j.Logger;

public interface SwitchableDevice {

    default DeviceState currentState() {
        return PinJokey.ONE.getDevicePinState(getType());
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
