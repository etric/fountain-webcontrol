package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.ValidationUtils;
import com.e3k.fountain.webcontrol.uart.UartMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SoundIndicatorDevice {
    ONE;

    SoundIndicatorDevice() {
        setStopped();
    }

    public void setPlayingItem(int itemNum) {
        ValidationUtils.requireInRange(itemNum, 0, 24, "playingItem");
        UartMessage.ONE.setPauseState(false);
        UartMessage.ONE.setCurrPlayingItem(itemNum + 1);
    }

    //TODO CLARIFY! pass 0 or 1? On pause or when stopped?
    public void setPausing() {
        UartMessage.ONE.setPauseState(true);
    }

    public void setStopped() {
        UartMessage.ONE.setPauseState(false);
        UartMessage.ONE.setCurrPlayingItem(0);
    }
}
