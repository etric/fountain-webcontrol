package com.e3k.fountain.webcontrol.alarm.handler;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.io.FountainDevice;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FountainStartAlarmListener implements AlarmListener {

    @Override
    public void handleAlarm(AlarmEntry alarmEntry) {
        log.info("Alarm triggered");
        FountainDevice.ONE.switchState(DeviceState.on);
    }
}
