package com.e3k.fountain.webcontrol.alarm.handler;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.io.SwitchableDevice;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EndAlarmListener implements AlarmListener {

    private final SwitchableDevice device;

    @Override
    public void handleAlarm(AlarmEntry alarmEntry) {
        log.info("Alarm triggered: {} -> OFF", device);
        device.switchState(DeviceState.off);
    }
}
