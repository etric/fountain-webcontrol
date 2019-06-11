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

    private final ConcurrentMap<DeviceType, GpioPinDigital> pinMappings = new ConcurrentHashMap<>();

    PinJokey() {
        if (SystemInfo.getOsName().startsWith("Linux")) {
            GpioController gpio = GpioFactory.getInstance();
            //OUTPUT
            pinMappings.put(DeviceType.fountain, gpio.provisionDigitalOutputPin(initPin(DeviceType.fountain)));
            pinMappings.put(DeviceType.light, gpio.provisionDigitalOutputPin(initPin(DeviceType.light)));
            pinMappings.put(DeviceType.sound, gpio.provisionDigitalOutputPin(initPin(DeviceType.sound)));
            pinMappings.put(DeviceType.soundFreqGen, gpio.provisionDigitalOutputPin(initPin(DeviceType.soundFreqGen)));
            pinMappings.put(DeviceType.soundIndicator, gpio.provisionDigitalOutputPin(initPin(DeviceType.soundIndicator)));
            pinMappings.put(DeviceType.auxGpio1, gpio.provisionDigitalOutputPin(initPin(DeviceType.auxGpio1)));
            pinMappings.put(DeviceType.auxGpio2, gpio.provisionDigitalOutputPin(initPin(DeviceType.auxGpio2)));
            pinMappings.put(DeviceType.auxGpio3, gpio.provisionDigitalOutputPin(initPin(DeviceType.auxGpio3)));
            pinMappings.put(DeviceType.auxGpio4, gpio.provisionDigitalOutputPin(initPin(DeviceType.auxGpio4)));
            pinMappings.put(DeviceType.auxGpio5, gpio.provisionDigitalOutputPin(initPin(DeviceType.auxGpio5)));
            pinMappings.put(DeviceType.auxGpio6, gpio.provisionDigitalOutputPin(initPin(DeviceType.auxGpio6)));
            //INPUT
            pinMappings.put(DeviceType.soundExtCtrl, gpio.provisionDigitalInputPin((initPin(DeviceType.soundExtCtrl))));
        } else {
            LoggerFactory.getLogger(PinJokey.class).error("Not supported on Windows!");
        }
    }

    public void setDevicePinState(DeviceType device, DeviceState state) {
        requireNonNull(device);
        requireNonNull(state);
        log.info("Updating device PIN state: {} --> {}", device, state);
        GpioPinDigitalOutput pin = tryGetOutputPin(device);
        if (pin != null) {
            pin.setState(DeviceState.on == state);
        }
    }

    public DeviceState getDevicePinState(DeviceType device) {
        requireNonNull(device);
        log.info("Getting device PIN state: {}", device);
        GpioPinDigital pin = tryGetDigitalPin(device);
        if (pin == null) {
            return DeviceState.off;
        }
        return DeviceState.fromBool(pin.getState().isHigh());
    }

    public GpioPinDigitalOutput tryGetOutputPin(DeviceType device) {
        return (GpioPinDigitalOutput) tryGetDigitalPin(device);
    }

    public GpioPinDigitalInput tryGetInputPin(DeviceType device) {
        return (GpioPinDigitalInput) tryGetDigitalPin(device);
    }

    public GpioPinDigital tryGetDigitalPin(DeviceType device) {
        GpioPinDigital pin = pinMappings.get(device);
        if (pin == null) {
            log.error("Cannot find PIN for device {}", device);
            return null;
        }
        log.debug("Device PIN for {}: {}", device, pin);
        return pin;
    }

    private static Pin initPin(DeviceType deviceType) {
        int pinAddress = PropertiesManager.ONE.getDevicePin(deviceType);
        return RaspiPin.getPinByAddress(pinAddress);
    }
}
