package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.CommonUtils;
import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.FountainDevice;
import com.e3k.fountain.webcontrol.io.LightDevice;
import com.e3k.fountain.webcontrol.io.SoundDevice;
import com.e3k.fountain.webcontrol.sysdatetime.SysDateTimeManager;
import com.e3k.fountain.webcontrol.uart.UartMessage;
import lombok.extern.slf4j.Slf4j;
import spark.utils.IOUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    private static void setupEndpoints() {

        before((request, response) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(request.requestMethod());
            sb.append(" " + request.url());
            sb.append(" " + request.body());
            log.info("REQUEST: " + sb.toString());
        });

        // FILES (e.g. logo)
        get("api/file/:fileName", ((request, response) -> {
            try (InputStream inputStream = new FileInputStream(request.params("fileName"));
                 OutputStream outputStream = response.raw().getOutputStream()
            ) {
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            }
            return response.raw();
        }));

        // DEVICES MAP
        get("/api/devicesMap", (request, response) -> {
            Map<DeviceType, Map<String, Object>> devicesMap = new HashMap<>();
            for (DeviceType deviceType : DeviceType.values()) {
                if (deviceType.isSoundRelated() && !PropertiesManager.ONE.isSoundDevicesEnabled()) {
                    continue; //skip
                }
                Map<String, Object> data = new HashMap<>();
                data.put("label", PropertiesManager.ONE.getLabel(deviceType));
                data.put("pin", PropertiesManager.ONE.getDevicePin(deviceType));
                data.put("state", PropertiesManager.ONE.getDeviceManualState(deviceType));
                devicesMap.put(deviceType, data);
            }
            return devicesMap;
        }, JsonResponseTransformer.ONE);

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

        // CONTROL MODE AUTO/MANUAL
        get("/api/mode", (request, response) -> {
            final ControlMode controlMode = PropertiesManager.ONE.getControlMode();
            response.status(200);
            return controlMode.name();
        });
        put("/api/mode/:modeAuto", (request, response) -> {
            final ControlMode mode = ControlMode.valueOf(request.params(":modeAuto"));
            final ControlMode oldMode = PropertiesManager.ONE.getControlMode();
            if (mode != oldMode) {
                log.info("Changing Control Mode: {}", mode);
                if (ControlMode.auto == mode) {
                    if (PropertiesManager.ONE.isPlayFromStartOnModeSwitch()) {
                        UartMessage.ONE.setCurrPlayingItem(0);
                    }
                    AlarmClock.ONE.turnOn();
                } else if (ControlMode.manual == mode){
                    AlarmClock.ONE.turnOff();
                }
                PropertiesManager.ONE.setControlMode(mode);
            }
            response.status(200);
            return "OK";
        });

        // ALARMS CLOCK
        get("/api/alarm/:alarmName", (request, response) -> {
            final AlarmType alarmType = AlarmType.valueOf(request.params(":alarmName"));
            checkSoundRelated(alarmType);
            final DaysWeekMap<String> weekAlarms = AlarmClock.ONE.getWeekAlarms(alarmType);
            response.status(200);
            return weekAlarms;
        }, JsonResponseTransformer.ONE);
        put("/api/alarm/:alarmName/:dayOfWeek", (request, response) -> {
            final AlarmType alarmType = AlarmType.valueOf(request.params(":alarmName"));
            checkSoundRelated(alarmType);
            final DayOfWeek dayOfWeek = DayOfWeek.valueOf(request.params(":dayOfWeek"));
            try {
                LocalTime time = CommonUtils.stringToTime(request.body());
                AlarmClock.ONE.updateTime(dayOfWeek, alarmType, time);
                response.status(200);
                return "OK";
            } catch (DateTimeParseException ex) {
                response.status(400);
                return "Wrong time format (HH:mm)";
            }
        });

        // DEVICES ON/OFF
        get("/api/device/realStates", ((request, response) -> {
            Map<DeviceType, Boolean> realStates = new HashMap<>();
            realStates.put(DeviceType.fountain, FountainDevice.ONE.currentState().toBool());
            realStates.put(DeviceType.light, LightDevice.ONE.currentState().toBool());
            realStates.put(DeviceType.sound, SoundDevice.ONE.currentState().toBool());
            return realStates;
        }), JsonResponseTransformer.ONE);
        get("/api/device/:deviceType", (request, response) -> {
            final DeviceType deviceType = DeviceType.valueOf(request.params(":deviceType"));
            checkSoundRelated(deviceType);
            return PropertiesManager.ONE.getDeviceManualState(deviceType);
        });
        put("/api/device/:deviceType/:modeOn", (request, response) -> {
            final DeviceType deviceType = DeviceType.valueOf(request.params(":deviceType"));
            checkSoundRelated(deviceType);
            final DeviceState deviceState = DeviceState.valueOf(request.params(":modeOn"));
            PropertiesManager.ONE.setDeviceManualState(deviceType, deviceState);
            CommonUtils.deviceByType(deviceType).switchState(deviceState);
            response.status(200);
            return "OK";
        });

        SettingsEndpoints.init();
        UmfEndpoints.init();

        if (PropertiesManager.ONE.isSoundDevicesEnabled()) {
            MusicEndpoints.init();
        }
    }

    private static void checkSoundRelated(AlarmType alarmType) {
        if (alarmType.isSoundRelated() && !PropertiesManager.ONE.isSoundDevicesEnabled()) {
            throw new IllegalArgumentException("Будильники музыки не подерживаются");
        }
    }

    private static void checkSoundRelated(DeviceType deviceType) {
        if (deviceType.isSoundRelated() && !PropertiesManager.ONE.isSoundDevicesEnabled()) {
            throw new IllegalArgumentException("Функции музыки не подерживаются");
        }
    }
}
