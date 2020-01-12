package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import com.e3k.fountain.webcontrol.uart.UartMessage;
import com.pi4j.io.serial.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.e3k.fountain.webcontrol.config.ConfigUtils.alarmsConfigByAlarmType;
import static java.util.Objects.requireNonNull;

/**
 * @author Alexander 'etric' Khamylov
 */
@Slf4j
public enum PropertiesManager implements Initializable {

    //TODO replace with Apache Commons Configuration w/ autoSave option

    ONE;

    private AppConfig appConfig;

    @Override
    public synchronized void init() {
        appConfig = ConfigIO.ONE.loadConfig();
    }

    // GETTERS
    // GETTERS
    // GETTERS
    // GETTERS

    public String dumpAppConfig() {
        return ConfigIO.ONE.dumpConfig();
    }

    public Map<DayOfWeek,  LocalTime> getAlarmClocks(AlarmType alarmType) {
        requireNonNull(alarmType);
        DaysWeekMap<LocalTime> result = alarmsConfigByAlarmType(appConfig.getDevices(), alarmType);
        return Collections.unmodifiableMap(result);
    }

    public ControlMode getControlMode() {
        return appConfig.getControlMode();
    }

    public DeviceState getDeviceManualState(@NonNull DeviceType deviceType) {
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

    public int getDevicePin(@NonNull DeviceType deviceType) {
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

    public synchronized void setAlarmClock(@NonNull AlarmType alarmType, DayOfWeek dayOfWeek, LocalTime time) {
        if (dayOfWeek != null && time != null) {
            alarmsConfigByAlarmType(appConfig.getDevices(), alarmType).put(dayOfWeek, time);
        }
    }

    public synchronized void setControlMode(@NonNull ControlMode controlMode) {
        appConfig.setControlMode(controlMode);
    }

    public synchronized void setDeviceManualState(@NonNull DeviceType deviceType, @NonNull DeviceState deviceState) {
        appConfig.getDevices().get(deviceType).setState(deviceState);
    }

    public synchronized void setLastPlayedItem(int lastPlayedItem) {
        if (PlaylistUtils.isValidMusicNum(lastPlayedItem)) {
            appConfig.setLastPlayedItem(lastPlayedItem);
        }
    }

    public synchronized void setVolume(int volume) {
        if (volume > 0 && volume <= 100) {
            appConfig.setVolume(volume);
        }
    }

    public synchronized void setPauseBetweenTracks(int pauseBetweenTracks) {
        if (pauseBetweenTracks >= 0 && pauseBetweenTracks <= 300) {
            appConfig.setPauseBetweenTracks(pauseBetweenTracks);
        }
    }

    public synchronized void setUmfBulbSwitchState(int bulbNum, DeviceState switchState) {
        requireNonNull(switchState);
        if (bulbNum >= 0 && bulbNum < 16) {
            appConfig.getUmf().getBulbs().get(bulbNum).setSwitchState(switchState);
            boolean setBit = switchState == DeviceState.on;
            UartMessage.ONE.setUmfBulb(bulbNum, setBit);
        }
    }

    public SettingsConfig getSettingsConfig() {
        return appConfig.getSettings();
    }

    public UmfConfig getUmfConfig() {
        return appConfig.getUmf();
    }
}
