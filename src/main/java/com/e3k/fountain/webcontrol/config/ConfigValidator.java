package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import com.pi4j.io.serial.*;
import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.e3k.fountain.webcontrol.config.ConfigUtils.*;
import static java.util.Objects.requireNonNull;

@UtilityClass
class ConfigValidator {

    static void validate(AppConfig appConfig) {
        validateGeneral(appConfig);
        validateUmf(appConfig);
        validateDevices(appConfig);
        validatePins(appConfig);
    }

    private static void validateGeneral(AppConfig appConfig) {
        requireNonNull(appConfig.getObject(), "object");
        requireNonNull(appConfig.getControlMode(), "controlMode");
        requireInRange(appConfig.getPauseBetweenTracks(), 0, 300, "pauseBetweenTracks");
        requireInRange(appConfig.getLastPlayedItem(), -1, PlaylistUtils.PLAYLIST_SIZE - 1, "lastPlayedItem");
        requireInRange(appConfig.getBlinkDelayMs(), 1, Integer.MAX_VALUE, "blinkDelayMs");
        requireInRange(appConfig.getHttpPort(), 0, Integer.MAX_VALUE, "httpPort");
        requireInRange(appConfig.getVolume(), 1, 100, "volume");
    }

    private static void validateUmf(AppConfig appConfig) {
        requireNonNull(appConfig.getUmf(), "umf");
        requireNonNull(appConfig.getUmf().getPassword(), "umf.password");
        if (Utils.isRaspberry()) {
            requireNonNull(Baud.getInstance(appConfig.getUmf().getBaudRate()), "umf.baudRate");
            requireNonNull(DataBits.getInstance(appConfig.getUmf().getDataBits()), "umf.dataBits");
            requireNonNull(Parity.getInstance(appConfig.getUmf().getParity()), "umf.parity");
            requireNonNull(StopBits.getInstance(appConfig.getUmf().getStopBits()), "umf.stopBits");
            requireNonNull(FlowControl.getInstance(appConfig.getUmf().getFlowControl()), "umf.flowControl");
        }

        requireNonNull(appConfig.getUmf().getBulbs(), "umf.bulbs");
        requireState(appConfig.getUmf().getBulbs().size() == 16, "not 16 umf.bulbs");
        final Set<String> bulbNames = new HashSet<>();
        for (int i = 1; i <= 16; i++) {
            UmfBulbConfig bulbConfig = appConfig.getUmf().getBulbs().get(i - 1);
            requireNonNull(bulbConfig.getLabel(), "umf.bulb[" + i + "].label");
            requireNonNull(bulbConfig.getSwitchState(), "umf.bulb[" + i + "].switchState");
            if (bulbNames.contains(bulbConfig.getLabel())) {
                throw new IllegalStateException("Bulb " + i + " has duplicated label");
            }
            bulbNames.add(bulbConfig.getLabel());
        }

        requireNonNull(appConfig.getUmf().getEmail(), "umf.email");
        requireNonNull(appConfig.getUmf().getEmail().getPassword(), "umf.email.password");
        requireNonNull(appConfig.getUmf().getEmail().getUsername(), "umf.email.username");
        requireAddress(appConfig.getUmf().getEmail().getSender(), "umf.email.sender");
        requireAddress(appConfig.getUmf().getEmail().getRecipients(), "umf.email.recipients");
    }

    private static void validateDevices(AppConfig appConfig) {
        requireNonNull(appConfig.getDevices(), "devices");
        Stream.of(DeviceType.values()).forEach(type -> {
            final DeviceConfig devCfg = appConfig.getDevices().get(type);
            requireNonNull(devCfg, type.toString());
            requireInRange(devCfg.getPin(), 0, 31, type + ".pin");
            requireNonNull(devCfg.getState(), type + ".state");
            requireNonNull(devCfg.getLabel(), type + ".label");
            if (Utils.isAlarmable(type)) {
                requireNonNull(devCfg.getAlarmsStart(), type + ".alarmsStart");
                requireNonNull(devCfg.getAlarmsEnd(), type + ".alarmsEnd");
                Stream.of(DayOfWeek.values()).forEach(dow -> {
                    final LocalTime startTime = devCfg.getAlarmsStart().get(dow);
                    final LocalTime endTime = devCfg.getAlarmsEnd().get(dow);
                    requireNonNull(startTime, type + ".alarmsStart." + dow);
                    requireNonNull(endTime, type + ".alarmsEnd." + dow);
                    requireState(startTime.isBefore(endTime),
                            type + ".alarmsStart." + dow + " is after " + type + ".alarmsEnd." + dow);
                });
            }
        });
    }

    private static void validatePins(AppConfig appConfig) {
        Set<Integer> assignedPins = new HashSet<>();
        for (DeviceConfig devCfg : appConfig.getDevices().values()) {
            if (assignedPins.contains(devCfg.getPin())) {
                throw new IllegalStateException("Pin " + devCfg.getPin() + " is assigned multiple times!");
            }
            assignedPins.add(devCfg.getPin());
        }
    }

}
