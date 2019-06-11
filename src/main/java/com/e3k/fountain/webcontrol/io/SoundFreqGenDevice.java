package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.concurrent.*;

@Slf4j
public enum SoundFreqGenDevice implements SwitchableDevice {
    ONE;

    private volatile Future<?> generatorFuture;

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
        if (generatorFuture != null && !generatorFuture.isCancelled()) {
            log.warn("Generator function is already running!");
            return;
        }
        GpioPinDigitalOutput pin = PinJokey.ONE.tryGetOutputPin(getType());
        if (pin != null) {
            log.info("Starting generator function");
            generatorFuture = pin.blink(100);
        }
    }

    public synchronized void stopBlinking() {
        if (generatorFuture == null || generatorFuture.isCancelled()) {
            log.warn("Generator function is NOT running!");
            return;
        }
        log.info("Stopping generator function");
        generatorFuture.cancel(true);
    }
}
