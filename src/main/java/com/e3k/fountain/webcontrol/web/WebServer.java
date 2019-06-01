package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.DaysWeekMap;
import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.AlarmType;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.io.FountainDevice;
import com.e3k.fountain.webcontrol.io.LightDevice;
import com.e3k.fountain.webcontrol.sysdatetime.SysDateTimeManager;
import lombok.extern.slf4j.Slf4j;

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
        // CONFIG
        get("/api/config", (request, response) -> {
            StringBuilder sb = new StringBuilder();
            HashMap<Object, Object> props = new HashMap<>(PropertiesManager.ONE.getProperties());
            for (Map.Entry entry : props.entrySet()) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append("<br/>");
            }
            return sb.toString();
        });

        // SYSTEM TIME
        get("/api/sysdatetime", (request, response) -> SysDateTimeManager.ONE.getTimeFormatted());
        put("/api/sysdatetime", (request, response) -> {
            SysDateTimeManager.ONE.parseAndUpdateTime(request.body());
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
                switch (deviceType) {
                    case light:
                        LightDevice.ONE.switchState(deviceState);
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
