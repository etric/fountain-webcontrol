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
    public DeviceState currentState() {
        //TODO keep in mind this hack (relying on player?)
        return DeviceState.fromBool(MusicPlayer.ONE.isPlaying());
//        return SwitchableDevice.super.currentState();
    }

    @Override
    public void switchState(DeviceState deviceState) {
        switchState(deviceState, false);
    }

    public void switchState(DeviceState deviceState, boolean playFromStart) {
        if (DeviceState.on == SoundExtCtrlDevice.ONE.currentState()) {
            log.debug("Skipping user/alarm state change because of active Sound External Control");
            return;
        }
        SwitchableDevice.super.switchState(deviceState);
        if (DeviceState.on == deviceState) {
            if (playFromStart) {
                MusicPlayer.ONE.startPlaylistFromBegin();
            } else {
                MusicPlayer.ONE.startPlaylistWhereLeft();
            }
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
