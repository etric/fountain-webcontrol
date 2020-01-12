package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.uart.UartMessage;
import org.slf4j.Logger;

public interface SwitchableDevice {

    default DeviceState currentState() {
        return UartMessage.ONE.getDevicesState(getType());
//        return PropertiesManager.ONE.getDeviceManualState(getType()); // dirty fallback check
    }

    default void switchState(DeviceState deviceState) {
        if (!isForceOff()) {//TODO check forceOff only for auto mode
            getLogger().info("Switching {} {}", getType(), deviceState);
            UartMessage.ONE.setDeviceState(getType(), deviceState);
        }
    }

    default void restoreState() {
        DeviceState manualState = PropertiesManager.ONE.getDeviceManualState(getType());
        if (currentState() != manualState) {
            getLogger().info("Trying to restore {} state", getType());
            switchState(manualState);
        }
    }

    default boolean isForceOff() {
        return PropertiesManager.ONE.getDeviceManualState(getType()) == DeviceState.off
            && PropertiesManager.ONE.getControlMode() == ControlMode.auto;
    }

    DeviceType getType();

    Logger getLogger();
}
