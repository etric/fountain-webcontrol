package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.pi4j.io.gpio.*;
import com.pi4j.system.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

@Slf4j
enum PinJokey {

    ONE;

    private final ConcurrentMap<DeviceType, GpioPinDigitalOutput> pinMappings = new ConcurrentHashMap<>();

    PinJokey() {
        if (SystemInfo.getOsName().startsWith("Linux")) {
            GpioController gpio = GpioFactory.getInstance();
            pinMappings.put(DeviceType.fountain, gpio.provisionDigitalOutputPin(initPin(DeviceType.fountain)));
            pinMappings.put(DeviceType.light, gpio.provisionDigitalOutputPin(initPin(DeviceType.light)));
            pinMappings.put(DeviceType.sound, gpio.provisionDigitalOutputPin(initPin(DeviceType.sound)));
        } else {
            LoggerFactory.getLogger(PinJokey.class).error("Not supported on Windows!");
        }
    }

    public void setDevicePinState(DeviceType device, DeviceState state) {
        requireNonNull(device);
        requireNonNull(state);
        log.info("Updating device PIN state: {} --> {}", device, state);
        GpioPinDigitalOutput pin = tryGetPin(device);
        if (pin != null) {
            pin.setState(DeviceState.on == state);
        }
    }

    public DeviceState getDevicePinState(DeviceType device) {
        requireNonNull(device);
        log.info("Getting device PIN state: {}", device);
        GpioPinDigitalOutput pin = tryGetPin(device);
        if (pin == null) {
            return DeviceState.off;
        }
        return DeviceState.fromBool(pin.getState().isHigh());
    }

    private GpioPinDigitalOutput tryGetPin(DeviceType device) {
        GpioPinDigitalOutput pin = pinMappings.get(device);
        if (pin == null) {
            log.error("Cannot find PIN for device {}", device);
            return null;
        }
        return pin;
    }

    private static Pin initPin(DeviceType deviceType) {
        int pinAddress = PropertiesManager.ONE.getDevicePin(deviceType);
        return RaspiPin.getPinByAddress(pinAddress);
    }
}
