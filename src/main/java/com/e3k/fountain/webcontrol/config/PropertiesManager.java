package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalTime;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * @author Alexander 'etric' Khamylov
 */
@Slf4j
public enum PropertiesManager {

    //TODO replace with Apache Commons Configuration w/ autoSave option

    ONE;

    private static final String CONTROL_MODE = "controlMode";
    private static final String LAST_PLAYED_ITEM = "lastPlayedItem";
    private static final String VOLUME = "volume";
    private static final String FOUNTAIN_PIN = "fountainPin";
    private static final String LIGHT_PIN = "lightPin";
    private static final String SOUND_PIN = "soundPin";
    private static final String HTTP_PORT = "httpPort";

    private interface DefaultPropValues {
        ControlMode CONTROL_MODE = ControlMode.manual;
        DeviceState DEVICE_STATE = DeviceState.off;
        int LAST_PLAYED_ITEM = -1;
        int VOLUME = 50;
        int FOUNTAIN_PIN = 0;
        int LIGHT_PIN = 1;
        int SOUND_PIN = 2;
        int HTTP_PORT = 9090;
    }

    private static final String CONFIG_FILE = "config.properties";

    private final Properties props;
    private final File propsFile;

    PropertiesManager() {
        props = new Properties();
        propsFile = new File(CONFIG_FILE);
        loadProperties();
    }

    public LocalTime getAlarmClock(AlarmType alarmType) {
        requireNonNull(alarmType);
        String value = getProp(alarmType.name());
        if (value == null) {
            return null;
        }
        try {
            return Utils.stringToTime(value);
        } catch (Exception ex) {
            log.warn("Corrupted value " + value + " for property " + alarmType, ex);
            return null;
        }
    }

    public void setAlarmClock(AlarmType alarmType, LocalTime localTime) {
        requireNonNull(alarmType);
        if (localTime != null) {
            setProp(alarmType.name(), Utils.timeToString(localTime));
        }
    }

    public ControlMode getControlMode() {
        final String controlModeStr = getProp(CONTROL_MODE);
        if (controlModeStr == null) {
            return DefaultPropValues.CONTROL_MODE;
        }
        return ControlMode.valueOf(controlModeStr);
    }

    public void setControlMode(ControlMode controlMode) {
        requireNonNull(controlMode);
        setProp(CONTROL_MODE, controlMode.name());
    }

    public DeviceState getDeviceManualState(DeviceType deviceType) {
        requireNonNull(deviceType);
        final String deviceState = getProp(deviceType.name());
        if (deviceState == null) {
            return DefaultPropValues.DEVICE_STATE;
        }
        return DeviceState.valueOf(deviceState);
    }

    public void setDeviceManualState(DeviceType deviceType, DeviceState deviceState) {
        requireNonNull(deviceType);
        requireNonNull(deviceState);
        setProp(deviceType.name(), deviceState.name());
    }

    public int getLastPlayedItem() {
        return getIntProp(LAST_PLAYED_ITEM, DefaultPropValues.LAST_PLAYED_ITEM);
    }

    public void setLastPlayedItem(int lastPlayedItem) {
        if (PlaylistUtils.isValidMusicNum(lastPlayedItem)) {
            setIntProp(LAST_PLAYED_ITEM, lastPlayedItem);
        }
    }

    public int getVolume() {
        return getIntProp(VOLUME, DefaultPropValues.VOLUME);
    }

    public void setVolume(int volume) {
        setIntProp(VOLUME, volume);
    }

    public int getDevicePin(DeviceType deviceType) {
        requireNonNull(deviceType);
        switch (deviceType) {
            case fountain:
                return getIntProp(FOUNTAIN_PIN, DefaultPropValues.FOUNTAIN_PIN);
            case light:
                return getIntProp(LIGHT_PIN, DefaultPropValues.LIGHT_PIN);
            case sound:
                return getIntProp(SOUND_PIN, DefaultPropValues.SOUND_PIN);
        }
        throw new IllegalStateException();
    }

    public int getPort() {
        return getIntProp(HTTP_PORT, DefaultPropValues.HTTP_PORT);
    }

    private void setProp(String property, String value) {
        requireNonNull(property);
        props.setProperty(property, value);
        log.debug("Set property {} = {}", property, value);
        storeProperties();
    }

    private String getProp(String property, String defaultValue) {
        requireNonNull(property);
        String value = props.getProperty(property);
        log.debug("Get property {} = {}", property, value);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    private String getProp(String property) {
        return getProp(property, null);
    }

    private void setIntProp(String property, int value) {
        setProp(property, String.valueOf(value));
    }

    private int getIntProp(String property, int defaultValue) {
        String valStr = getProp(property, null);
        if (valStr == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(valStr);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    private void storeProperties() {
        try (OutputStream propsOut = new FileOutputStream(propsFile)) {
            props.store(propsOut, "Fountain Web-control configuration file");
            log.debug("Properties successfully stored");
        } catch (IOException ex) {
            log.error("Failed storing properties", ex);
        }
    }

    private void loadProperties() {
        final Logger _log = LoggerFactory.getLogger(PropertiesManager.class);
        _log.info("Loading properties from {}", CONFIG_FILE);
        if (propsFile.exists()) {
            try (InputStream propsIn = new FileInputStream(propsFile)) {
                props.load(propsIn);
                _log.info("Properties successfully loaded");
            } catch (IOException ex) {
                _log.error("Failed loading properties", ex);
            }
        } else {
            try {
                propsFile.createNewFile();
            } catch (IOException ex) {
                _log.error("Failed creating properties file", ex);
            }
        }
    }
}