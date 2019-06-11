package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
    private static final String HTTP_PORT = "httpPort";
    private static final String PAUSE_BETWEEN_TRACKS = "pauseBetweenTracks";

    private static final String CONFIG_FILE = "config.properties";

    private final Properties props;
    private final File propsFile;

    PropertiesManager() {
        props = new Properties(getDefaultProperties());
        propsFile = new File(CONFIG_FILE);
        loadProperties();
    }

    public Properties getProperties() {
        return props;
    }

    public DaysWeekMap<LocalTime> getAlarmClocks(AlarmType alarmType) {
        requireNonNull(alarmType);
        String value = getProp(alarmType.name());
        if (value == null) {
            return new DaysWeekMap<>();
        }
        try {
            return DaysWeekMap.deserialize(Utils::stringToTime, value);
        } catch (Exception ex) {
            log.warn("Corrupted value " + value + " for property " + alarmType, ex);
            return new DaysWeekMap<>();
        }
    }

    public void setAlarmClocks(AlarmType alarmType, DaysWeekMap<LocalTime> daysWeekMap) {
        requireNonNull(alarmType);
        if (daysWeekMap != null && !daysWeekMap.isEmpty()) {
            setProp(alarmType.name(), daysWeekMap.serialize(Utils::timeToString)); //TODO
        }
    }

    public void setAlarmClock(AlarmType alarmType, DayOfWeek dayOfWeek, LocalTime time) {
        //TODO !!!!!!!!!!!!! don't call GET - it causes full deserialization each time!
        DaysWeekMap<LocalTime> map = getAlarmClocks(alarmType);
        map.put(dayOfWeek, time);
        setAlarmClocks(alarmType, map);
    }

    public ControlMode getControlMode() {
        final String controlModeStr = getProp(CONTROL_MODE);
        return ControlMode.valueOf(controlModeStr);
    }

    public void setControlMode(ControlMode controlMode) {
        requireNonNull(controlMode);
        setProp(CONTROL_MODE, controlMode.name());
    }

    public DeviceState getDeviceManualState(DeviceType deviceType) {
        requireNonNull(deviceType);
        final String deviceState = getProp(deviceType.name());
        return DeviceState.valueOf(deviceState);
    }

    public void setDeviceManualState(DeviceType deviceType, DeviceState deviceState) {
        requireNonNull(deviceType);
        requireNonNull(deviceState);
        setProp(deviceType.name(), deviceState.name());
    }

    public int getLastPlayedItem() {
        return getIntProp(LAST_PLAYED_ITEM);
    }

    public void setLastPlayedItem(int lastPlayedItem) {
        if (PlaylistUtils.isValidMusicNum(lastPlayedItem)) {
            setIntProp(LAST_PLAYED_ITEM, lastPlayedItem);
        }
    }

    public int getVolume() {
        return getIntProp(VOLUME);
    }

    public void setVolume(int volume) {
        setIntProp(VOLUME, volume);
    }

    public int getPauseBetweenTracks() {
        return getIntProp(PAUSE_BETWEEN_TRACKS);
    }

    public void setPauseBetweenTracks(int pauseBetweenTracks) {
        setIntProp(PAUSE_BETWEEN_TRACKS, pauseBetweenTracks);
    }

    public int getDevicePin(DeviceType deviceType) {
        requireNonNull(deviceType);
        return getIntProp(deviceType.name() + "Pin");
    }

    public int getPort() {
        return getIntProp(HTTP_PORT);
    }

    public String getLabel(DeviceType deviceType) {
        return getProp(deviceType.name() + "Label");
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

    private int getIntProp(String property) {
        String valStr = getProp(property);
        return Integer.parseInt(valStr);
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
            try (Reader reader = Files.newBufferedReader(propsFile.toPath(), Charset.forName("UTF-8"))) {
                props.load(reader);
                validatePins();
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

    private void validatePins() {
        Set<String> assignedPins = new HashSet<>();
        Enumeration<?> propNamesEnumeration = props.propertyNames();
        while (propNamesEnumeration.hasMoreElements()) {
            String propName = (String) propNamesEnumeration.nextElement();
            if (propName.endsWith("Pin")) {
                String pinProp = props.getProperty(propName);
                if (assignedPins.contains(pinProp)) {
                    throw new AssertionError("Same PIN is assigned to multiple devices!");
                }
                assignedPins.add(pinProp);
            }
        }
    }

    private static Properties getDefaultProperties() {
        Properties defaultProperties = new Properties();

        defaultProperties.setProperty(CONTROL_MODE, ControlMode.manual.name());
        defaultProperties.setProperty(LAST_PLAYED_ITEM, "-1");
        defaultProperties.setProperty(VOLUME, "50");
        defaultProperties.setProperty(HTTP_PORT, "80");
        defaultProperties.setProperty(PAUSE_BETWEEN_TRACKS, "0");

        int pinNo = 0;
        for (DeviceType deviceType : DeviceType.values()) {
            defaultProperties.setProperty(deviceType.name(), DeviceState.off.name());
            defaultProperties.setProperty(deviceType.name() + "Pin", String.valueOf(pinNo++));
        }

        return defaultProperties;
    }
}
