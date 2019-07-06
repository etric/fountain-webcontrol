package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.*;
import com.e3k.fountain.webcontrol.io.UartDevice;
import com.e3k.fountain.webcontrol.io.player.MusicPlayer;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import com.e3k.fountain.webcontrol.sysdatetime.SysDateTimeManager;
import lombok.extern.slf4j.Slf4j;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

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

    private static Route umfAuthenticated(Route route) {
        return (request, response) -> {
            final String pswd = request.headers("pswd");
            if (pswd == null || !pswd.equals(PropertiesManager.ONE.getUmfPassword())) {
                response.status(403);
                return "Неверный пароль!";
            } else {
                return route.handle(request, response);
            }
        };
    }

    private static void setupEndpoints() {
        // UMF
        get("api/umf/page", umfAuthenticated((request, response) ->
                WebServer.class.getClassLoader().getResourceAsStream("umf.html")));
        put("api/umf/bulb/:bulbNum/:switchState", umfAuthenticated((request, response) -> {
            final int bulbNum = Integer.parseInt(request.params(":bulbNum"));
            final DeviceState bulbState = DeviceState.valueOf(request.params(":switchState"));
            PropertiesManager.ONE.setUmfBulbSwitchState(bulbNum, bulbState);
            return "OK";
        }));
        // for initial load
        get("api/umf/bulb/list", umfAuthenticated((request, response) ->
                PropertiesManager.ONE.getAllUmfBulbInfo()), new JsonResponseTransformer());
        // for polling
        get("api/umf/bulb/states", umfAuthenticated((request, response) ->
                UartDevice.ONE.getBulbStates()), new JsonResponseTransformer());

        // DEVICES MAP
        get("/api/devicesMap", (request, response) -> {
            Map<DeviceType, Map<String, Object>> devicesMap = new HashMap<>();
            for (DeviceType deviceType : DeviceType.values()) {
                Map<String, Object> data = new HashMap<>();
                data.put("label", PropertiesManager.ONE.getLabel(deviceType));
                data.put("pin", PropertiesManager.ONE.getDevicePin(deviceType));
                data.put("state", PropertiesManager.ONE.getDeviceManualState(deviceType));
                devicesMap.put(deviceType, data);
            }
            return devicesMap;
        }, new JsonResponseTransformer());

        // CONFIG
        get("/api/config", (request, response) -> {
            response.type("application/json");
            return PropertiesManager.ONE.dumpAppConfig();
        });

        // SYSTEM TIME
        get("/api/sysdatetime", (request, response) -> SysDateTimeManager.ONE.getTimeFormatted());
        put("/api/sysdatetime", (request, response) -> {
            SysDateTimeManager.ONE.parseAndUpdateTime(request.body());
            AlarmClock.ONE.reInitAlarms();
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
        }, new JsonResponseTransformer());
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

        // PAUSE BETWEEN TRACKS
        get("/api/pauseBetweenTracks", (request, response) -> {
            String pauseBetweenTracks = String.valueOf(MusicPlayer.ONE.getPauseBetweenTracks());
            response.status(200);
            return pauseBetweenTracks;
        });
        put("/api/pauseBetweenTracks/:val", (request, response) -> {
            final int pauseBetweenTracks = Integer.valueOf(request.params(":val"));
            if (pauseBetweenTracks < 0 || pauseBetweenTracks > 300) {
                response.status(400);
                return "Pause Between Tracks must be within range 0..300";
            }
            MusicPlayer.ONE.changePauseBetweenTracks(pauseBetweenTracks);
            response.status(200);
            return "OK";
        });

        // ALARMS CLOCK
        get("/api/alarm/:alarmName", (request, response) -> {
            final AlarmType alarmType = AlarmType.valueOf(request.params(":alarmName"));
            final DaysWeekMap<String> weekAlarms = AlarmClock.ONE.getWeekAlarms(alarmType);
            response.status(200);
            return weekAlarms;
        }, new JsonResponseTransformer());
        put("/api/alarm/:alarmName/:dayOfWeek", (request, response) -> {
            final AlarmType alarmType = AlarmType.valueOf(request.params(":alarmName"));
            final DayOfWeek dayOfWeek = DayOfWeek.valueOf(request.params(":dayOfWeek"));
            try {
                LocalTime time = Utils.stringToTime(request.body());
                AlarmClock.ONE.updateTime(dayOfWeek, alarmType, time);
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
                Utils.deviceByType(deviceType).switchState(deviceState);
            }
            response.status(200);
            return "OK";
        });
    }
}
