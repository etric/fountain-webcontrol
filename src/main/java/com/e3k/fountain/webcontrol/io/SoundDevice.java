package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.player.MusicPlayer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public enum SoundDevice implements SwitchableDevice {
    ONE;

    @Override
    public void switchState(DeviceState deviceState) {
        SwitchableDevice.super.switchState(deviceState);
        if (DeviceState.on == deviceState) {
            MusicPlayer.ONE.startPlaylistWhereLeft();
        } else {
            MusicPlayer.ONE.stopPlaying();
        }
    }

    @Override
    public DeviceType getType() {
        return DeviceType.sound;
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
