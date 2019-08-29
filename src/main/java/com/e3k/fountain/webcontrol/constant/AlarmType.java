package com.e3k.fountain.webcontrol.constant;

public enum AlarmType {
    fountainAlarmStart, fountainAlarmEnd,
    lightAlarmStart, lightAlarmEnd,
    soundAlarmStart, soundAlarmEnd;

    public boolean isSoundRelated() {
        return this == soundAlarmEnd || this == soundAlarmStart;
    }
}
