package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.constant.*;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import com.google.gson.*;
import com.pi4j.io.serial.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.e3k.fountain.webcontrol.config.ConfigUtils.*;
import static com.e3k.fountain.webcontrol.config.ConfigValidator.validate;
import static java.util.Objects.requireNonNull;

/**
 * @author Alexander 'etric' Khamylov
 */
@Slf4j
public enum PropertiesManager implements Initializable {

    //TODO replace with Apache Commons Configuration w/ autoSave option

    ONE;

    private final byte[] uartUserMessage = new byte[] {0, 0, 0, 0, 0, 0, 0, 0}; //3,4-data

    private final Gson gson = buildGson();
    private final File configFile = new File("config.json");
    private AppConfig appConfig;

    @Override
    public synchronized void init() {
        if (appConfig == null) {
            loadConfig();
            log.info("Validating Config...");
            validate(this.appConfig);
            initUartUserMessage();
        }
    }

    private void initUartUserMessage() {
        uartUserMessage[0] = 'M';
        uartUserMessage[1] = 'V';
        uartUserMessage[2] = 'K';
        for (int i = 0; i < 16; i++) {
            boolean setBit = appConfig.getUmf().getBulbs().get(i).getSwitchState() == DeviceState.on;
            if (i < 8) {
                uartUserMessage[3] = setOrUnsetBit(uartUserMessage[3], i, setBit);
            } else {
                uartUserMessage[4] = setOrUnsetBit(uartUserMessage[4], i, setBit);
            }
        }
    }

    // GETTERS
    // GETTERS
    // GETTERS
    // GETTERS

    public String dumpAppConfig() {
        JsonObject jsonObject = (JsonObject) gson.toJsonTree(appConfig);
        jsonObject.addProperty("version", Utils.getAppVersion());
        return jsonObject.toString();
    }

    public Map<DayOfWeek,  LocalTime> getAlarmClocks(AlarmType alarmType) {
        requireNonNull(alarmType);
        DaysWeekMap<LocalTime> result = alarmsConfigByAlarmType(appConfig.getDevices(), alarmType);
        return Collections.unmodifiableMap(result);
    }

    public ControlMode getControlMode() {
        return appConfig.getControlMode();
    }

    public DeviceState getDeviceManualState(DeviceType deviceType) {
        requireNonNull(deviceType);
        return appConfig.getDevices().get(deviceType).getState();
    }

    public int getLastPlayedItem() {
        return appConfig.getLastPlayedItem();
    }

    public int getVolume() {
        return appConfig.getVolume();
    }

    public int getPauseBetweenTracks() {
        return appConfig.getPauseBetweenTracks();
    }

    public int getDevicePin(DeviceType deviceType) {
        requireNonNull(deviceType);
        return appConfig.getDevices().get(deviceType).getPin();
    }

    public int getPort() {
        return appConfig.getHttpPort();
    }

    public String getLabel(DeviceType deviceType) {
        return appConfig.getDevices().get(deviceType).getLabel();
    }

    public int getBlinkDelay() {
        return appConfig.getBlinkDelayMs();
    }

    public String getUmfPassword() {
        return appConfig.getUmf().getPassword();
    }

    public Baud getUmfBaudRate() {
        return Baud.getInstance(appConfig.getUmf().getBaudRate());
    }

    public DataBits getUmfDataBits() {
        return DataBits.getInstance(appConfig.getUmf().getDataBits());
    }

    public Parity getUmfParity() {
        return Parity.getInstance(appConfig.getUmf().getParity());
    }

    public StopBits getUmfStopBits() {
        return StopBits.getInstance(appConfig.getUmf().getStopBits());
    }

    public FlowControl getUmfFlowControl() {
        return FlowControl.getInstance(appConfig.getUmf().getFlowControl());
    }

    public InternetAddress getUmfEmailSender() {
        try {
            return new InternetAddress(appConfig.getUmf().getEmail().getSender());
        } catch (AddressException e) {
            throw new IllegalArgumentException("Invalid umf.email.sender");
        }
    }

    public InternetAddress[] getUmfEmailRecipients() {
        try {
            return InternetAddress.parse(appConfig.getUmf().getEmail().getRecipients());
        } catch (AddressException e) {
            throw new IllegalArgumentException("Invalid umf.email.recipients");
        }
    }

    public String getUmfEmailUsername() {
        return appConfig.getUmf().getEmail().getUsername();
    }

    public String getUmfEmailPassword() {
        return appConfig.getUmf().getEmail().getPassword();
    }

    public String getObjectName() {
        return appConfig.getObject();
    }

    public byte[] getUartUserMessage() {
        return uartUserMessage.clone();
    }

    public List<UmfBulbConfig> getAllUmfBulbInfo() {
        return Collections.unmodifiableList(appConfig.getUmf().getBulbs());
    }

    public UmfSmsConfig getUmfSmsConfig() {
        return appConfig.getUmf().getSms();
    }

    public boolean isPlayFromStartOnModeSwitch() {
        return appConfig.isPlayFromStartOnModeSwitch();
    }

    public boolean isSoundDevicesEnabled() {
        return appConfig.isSoundDevicesEnabled();
    }

    // SETTERS
    // SETTERS
    // SETTERS
    // SETTERS

    public synchronized void setAlarmClock(AlarmType alarmType, DayOfWeek dayOfWeek, LocalTime time) {
        requireNonNull(alarmType);
        if (dayOfWeek != null && time != null) {
            alarmsConfigByAlarmType(appConfig.getDevices(), alarmType).put(dayOfWeek, time);
            storeConfig();
        }
    }

    public synchronized void setControlMode(ControlMode controlMode) {
        requireNonNull(controlMode);
        appConfig.setControlMode(controlMode);
        storeConfig();
    }

    public synchronized void setDeviceManualState(DeviceType deviceType, DeviceState deviceState) {
        requireNonNull(deviceType);
        requireNonNull(deviceState);
        appConfig.getDevices().get(deviceType).setState(deviceState);
        storeConfig();
    }

    public synchronized void setLastPlayedItem(int lastPlayedItem) {
        if (PlaylistUtils.isValidMusicNum(lastPlayedItem)) {
            appConfig.setLastPlayedItem(lastPlayedItem);
            storeConfig();
        }
    }

    public synchronized void setVolume(int volume) {
        if (volume > 0 && volume <= 100) {
            appConfig.setVolume(volume);
            storeConfig();
        }
    }

    public synchronized void setPauseBetweenTracks(int pauseBetweenTracks) {
        if (pauseBetweenTracks >= 0 && pauseBetweenTracks <= 300) {
            appConfig.setPauseBetweenTracks(pauseBetweenTracks);
            storeConfig();
        }
    }

    public synchronized void setUmfBulbSwitchState(int bulbNum, DeviceState switchState) {
        requireNonNull(switchState);
        if (bulbNum >= 0 && bulbNum < 16) {
            appConfig.getUmf().getBulbs().get(bulbNum).setSwitchState(switchState);
            boolean setBit = switchState == DeviceState.on;
            if (bulbNum < 8) {
                uartUserMessage[3] = setOrUnsetBit(uartUserMessage[3], bulbNum, setBit);
            } else {
                uartUserMessage[4] = setOrUnsetBit(uartUserMessage[4], bulbNum, setBit);
            }
            storeConfig();
        }
    }

    // IO
    // IO
    // IO
    // IO

    private void storeConfig() {
        final Logger log = LoggerFactory.getLogger(PropertiesManager.class);
        try (Writer propsOut = new FileWriter(configFile)) {
            gson.toJson(appConfig, propsOut);
            log.trace("Properties successfully stored");
        } catch (IOException ex) {
            log.error("Failed storing properties", ex);
        }
    }

    private void loadConfig() {
        log.info("Loading config from {}", configFile.getName());
        if (configFile.exists()) {
            try (Reader reader = Files.newBufferedReader(configFile.toPath(), Charset.forName("UTF-8"))) {
                appConfig = gson.fromJson(reader, AppConfig.class);
                if (appConfig != null) {
                    log.info("Config successfully loaded");
                    return;
                }
            } catch (IOException ex) {
                log.error("Failed loading config from file", ex);
            }
        } else {
            try {
                configFile.createNewFile();
            } catch (IOException ex) {
                log.error("Failed creating config file", ex);
            }
        }
        log.warn("Storing default config as a template");
        appConfig = new AppConfig();
        storeConfig();
    }
}
