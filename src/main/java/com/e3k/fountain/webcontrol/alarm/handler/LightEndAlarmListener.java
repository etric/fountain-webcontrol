package com.e3k.fountain.webcontrol.alarm.handler;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.io.LightDevice;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LightEndAlarmListener implements AlarmListener {

    @Override
    public void handleAlarm(AlarmEntry alarmEntry) {
        log.info("Alarm triggered");
        LightDevice.ONE.switchState(DeviceState.off);
    }
}
