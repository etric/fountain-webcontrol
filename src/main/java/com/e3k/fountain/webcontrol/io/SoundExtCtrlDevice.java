package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public enum SoundExtCtrlDevice implements SwitchableDevice {

  ONE;

  private volatile DeviceState state = DeviceState.on;

  @Override
  public void switchState(DeviceState deviceState) {
    getLogger().info("Switching External Sound Control {}", deviceState);
    this.state = deviceState;
    if (deviceState == DeviceState.off) {
      log.info("Turning Sound OFF because of External Sound Control");
      SoundDevice.ONE.switchOffForcely();
    } else {
      //TODO restore manual state or re-sync with alarms
      log.info("Restoring Sound state because of External Sound Control");
      if (ControlMode.auto == PropertiesManager.ONE.getControlMode()) {
        AlarmClock.ONE.reSyncDeviceStateWithAlarms(SoundDevice.ONE);
      } else {
        DeviceState manState = PropertiesManager.ONE.getDeviceManualState(SoundDevice.ONE.getType());
        SoundDevice.ONE.switchState(manState);
      }
    }
  }

  @Override
  public DeviceState currentState() {
    return state;
  }

  @Override
  public void restoreState() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DeviceType getType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Logger getLogger() {
    return log;
  }
}
