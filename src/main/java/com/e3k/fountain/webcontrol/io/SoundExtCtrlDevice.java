package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public enum SoundExtCtrlDevice implements SwitchableDevice, Initializable {
    ONE;

    private final GpioPinListenerDigital extCtrlListener = extCtrlListener();

    @Override
    public void init() {
        GpioPinDigitalInput pin = PinJokey.ONE.tryGetInputPin(getType());
        if (pin == null) {
            log.warn("Sound External Control Pin was not found");
            return;
        }
        if (pin.hasListener(extCtrlListener)) {
            log.warn("Listener is already added");
            return;
        }
        log.info("Registering listener for Sound External Control Pin");
        pin.addListener(extCtrlListener);
        // Sync with External Control Pin current state
        if (DeviceState.on == currentState()) {
            log.info("Sending artificial GpioPinDigitalStateChangeEvent event to sync-up");
            GpioPinDigitalStateChangeEvent event = new GpioPinDigitalStateChangeEvent(new Object(), pin, PinState.HIGH);
            extCtrlListener.handleGpioPinDigitalStateChangeEvent(event);
        }
    }

    @Override
    public void restoreState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void switchState(DeviceState deviceState) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeviceState currentState() {
        return PinJokey.ONE.getDevicePinState(getType());
    }

    @Override
    public DeviceType getType() {
        return DeviceType.soundExtCtrl;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    private static GpioPinListenerDigital extCtrlListener() {
        return  event -> {
            if (event.getState().isHigh()) {
                SoundDevice.ONE.switchOffForcely();
            } else {
                //TODO restore manual state or re-sync with alarms
                log.info("Restoring state because External Control offed");
                if (ControlMode.auto == PropertiesManager.ONE.getControlMode()) {
                    AlarmClock.ONE.reSyncDeviceStateWithAlarms(SoundDevice.ONE);
                } else {
                    DeviceState manState = PropertiesManager.ONE.getDeviceManualState(SoundDevice.ONE.getType());
                    SoundDevice.ONE.switchState(manState);
                }
            }
        };
    }
}
