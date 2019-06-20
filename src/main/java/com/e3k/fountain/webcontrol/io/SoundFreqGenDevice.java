package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public enum SoundFreqGenDevice implements SwitchableDevice {
    ONE;

    @Override
    public DeviceType getType() {
        return DeviceType.soundFreqGen;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public DeviceState currentState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void switchState(DeviceState deviceState) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void restoreState() {
        throw new UnsupportedOperationException();
    }

    public synchronized void startBlinking() {
        GpioPinDigitalOutput pin = PinJokey.ONE.tryGetOutputPin(getType());
        if (pin != null) {
            log.info("Starting generator function");
            pin.blink(100);
        }
    }

    public synchronized void stopBlinking() {
        GpioPinDigitalOutput pin = PinJokey.ONE.tryGetOutputPin(getType());
        if (pin != null) {
            log.info("Stopping generator function");
            pin.blink(0);
            pin.low();
        }
    }
}
