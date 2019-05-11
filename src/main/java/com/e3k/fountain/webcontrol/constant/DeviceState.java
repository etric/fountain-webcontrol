package com.e3k.fountain.webcontrol.constant;

public enum DeviceState {
    on, off;

    public static DeviceState fromBool(boolean val) {
        return val ? on : off;
    }
}