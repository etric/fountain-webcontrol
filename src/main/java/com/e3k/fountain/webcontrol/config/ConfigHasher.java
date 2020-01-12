package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.uart.UartMessage;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static com.e3k.fountain.webcontrol.constant.DeviceType.*;

final class ConfigHasher {

  static int umfConfigHash(UmfConfig cfg) {
    int[] umfBulbsHashes = cfg.getBulbs().stream()
        .mapToInt(umfCfg -> Boolean.hashCode(umfCfg.getSwitchState().toBool()))
        .toArray();
    return Arrays.hashCode(umfBulbsHashes);
  }

  static int settingsConfigHash(SettingsConfig cfg) {
    return Objects.hash(
        cfg.getMotor(),
        cfg.getRed(),
        cfg.getGreen(),
        cfg.getBlue(),
        Arrays.hashCode(cfg.getAuxButtons()));
  }

  static int devicesConfigHash(Map<DeviceType, DeviceConfig> devicesCfg) {
    final DeviceType[] devices = new DeviceType[] { fountain, light, sound };
    final int[] hashes = new int[
        DayOfWeek.values().length * devices.length * 2 // alarms
        + devices.length //states
        ];
    Arrays.fill(hashes, 0);
    int counter = 0;
    for (DeviceType deviceType : devices) {
      final DeviceConfig cfg = devicesCfg.get(deviceType);
      hashes[counter++] = Boolean.hashCode(cfg.getState().toBool());
      for (DayOfWeek dow : DayOfWeek.values()) {
        if (cfg.getAlarmsStart() != null) {
          hashes[counter] = Objects.hashCode(cfg.getAlarmsStart().get(dow));
        }
        counter++;
        if (cfg.getAlarmsEnd() != null) {
          hashes[counter] = Objects.hashCode(cfg.getAlarmsEnd().get(dow));
        }
        counter++;
      }
    }
    return Arrays.hashCode(hashes);
  }

  static int uartMessageHash(UartMessage uartMessage) {
    byte[] um = uartMessage.getBytes();
    return Objects.hash(
        um[3], um[4], um[5], um[6], um[7], um[8], um[9], um[10],
        um[11], um[12], um[13], um[14], um[15], um[16], um[17]
    );
  }
}
