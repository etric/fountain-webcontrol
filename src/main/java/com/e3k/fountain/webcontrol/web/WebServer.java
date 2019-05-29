package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.FountainDevice;
import com.e3k.fountain.webcontrol.io.LightDevice;
import com.e3k.fountain.webcontrol.io.SoundDevice;
import com.e3k.fountain.webcontrol.io.player.MusicPlayer;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import com.e3k.fountain.webcontrol.sysdatetime.SysDateTimeManager;
import com.jsoniter.output.JsonStream;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.MultipartConfigElement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static spark.Spark.*;

@Slf4j
public class WebServer {

    public static void bootstrap() {
        log.info("Setting up web server");

        port(PropertiesManager.ONE.getPort());
        staticFiles.location("/web");
        setupEndpoints();

        log.info("Finished setting up web server");
    }

    private static void setupEndpoints() {
        // SYSTEM TIME
        get("/api/sysdatetime", (request, response) -> SysDateTimeManager.ONE.getTimeFormatted());
        put("/api/sysdatetime/:sysdatetime", (request, response) -> {
            LocalDateTime newDateTime = LocalDateTime.parse(request.params(":musicNum"));
            SysDateTimeManager.ONE.setTime(newDateTime);
            return "OK";
        });

        // MUSIC
        get("/api/music/currentPlayingItem", (request, response) -> {
            response.status(200);
            final int techNum = MusicPlayer.ONE.getCurrentPlayingItem();
            return techNum + 1;
        });
        get("/api/music/playlist", (request, response) -> {
            response.status(200);
            return MusicPlayer.ONE.getPlaylistItems();
        }, JsonStream::serialize);
        put("/api/music/:musicNum", (request, response) -> {
            final int realNum = Integer.parseInt(request.params(":musicNum"));
            final int techNum = realNum - 1;
            if (!PlaylistUtils.isValidMusicNum(techNum)) {
                response.status(400);
                return "Music # must be within range 1..20";
            }
            if (MusicPlayer.ONE.getCurrentPlayingItem() == techNum) {
                response.status(400);
                return "Music # is currently playing";
            }
            request.attribute("org.eclipse.jetty.multipartConfig",
                    new MultipartConfigElement("/temp"));

            MusicUploadHelper.upload(request.raw().getPart("file"), realNum);
            response.status(200);
            return "OK";
        });

        // CONTROL MODE AUTO/MANUAL
        get("/api/mode", (request, response) -> {
            final ControlMode controlMode = PropertiesManager.ONE.getControlMode();
            response.status(200);
            return controlMode.name();
        });
        put("/api/mode/:modeAuto", (request, response) -> {
            final ControlMode mode = ControlMode.valueOf(request.params(":modeAuto"));
            log.info("Changing Control Mode: {}", mode);
            if (ControlMode.auto == mode) {
                AlarmClock.ONE.turnOn();
            } else if (ControlMode.manual == mode){
                AlarmClock.ONE.turnOff();
            }
            PropertiesManager.ONE.setControlMode(mode);
            response.status(200);
            return "OK";
        });

        // VOLUME
        get("/api/volume", (request, response) -> {
            String vol = String.valueOf(MusicPlayer.ONE.getVolume());
            response.status(200);
            return vol;
        });
        put("/api/volume/:val", (request, response) -> {
            final int vol = Integer.valueOf(request.params(":val"));
            if (vol < 1 || vol > 100) {
                response.status(400);
                return "Volume must be within range 1..100";
            }
            MusicPlayer.ONE.changeVolume(vol);
            response.status(200);
            return "OK";
        });

        // ALARMS CLOCK
        get("/api/alarm/:alarmName", (request, response) -> {
            final AlarmType alarmType = AlarmType.valueOf(request.params(":alarmName"));
            final LocalTime time = AlarmClock.ONE.getTime(alarmType);
            final String timeStr = Utils.timeToString(time);
            response.status(200);
            return timeStr;
        });
        put("/api/alarm/:alarmName", (request, response) -> {
            final AlarmType alarmType = AlarmType.valueOf(request.params(":alarmName"));
            try {
                LocalTime time = Utils.stringToTime(request.body());
                AlarmClock.ONE.updateTime(alarmType, time);
                response.status(200);
                return "OK";
            } catch (DateTimeParseException ex) {
                response.status(400);
                return "Wrong time format (HH:mm)";
            }
        });

        // DEVICES ON/OFF
        get("/api/:deviceType", (request, response) -> {
            final DeviceType deviceType = DeviceType.valueOf(request.params(":deviceType"));
            return PropertiesManager.ONE.getDeviceManualState(deviceType);
        });
        put("/api/:deviceType/:modeOn", (request, response) -> {
            final DeviceType deviceType = DeviceType.valueOf(request.params(":deviceType"));
            final DeviceState deviceState = DeviceState.valueOf(request.params(":modeOn"));
            PropertiesManager.ONE.setDeviceManualState(deviceType, deviceState);
            if (PropertiesManager.ONE.getControlMode() == ControlMode.manual) {
                switch (deviceType) {
                    case light:
                        LightDevice.ONE.switchState(deviceState);
                        break;
                    case sound:
                        SoundDevice.ONE.switchState(deviceState);
                        break;
                    case fountain:
                        FountainDevice.ONE.switchState(deviceState);
                        break;
                }
            }
            response.status(200);
            return "OK";
        });
    }
}
