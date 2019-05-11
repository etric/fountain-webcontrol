package com.e3k.fountain.webcontrol;

import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.ControlMode;
import com.e3k.fountain.webcontrol.web.WebServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntryPoint {

    public static void main(String args[]) {

        // start web server
        WebServer.bootstrap();

//        if (args != null && args.length > 0) {
//            Utils.D = args[0].toLowerCase().equals("d");
//        }

        log.info("Bootstrapping AlarmClock state and Devices switches...");
        if (ControlMode.auto == PropertiesManager.ONE.getControlMode()) {
            AlarmClock.ONE.turnOn();
        } else {
            AlarmClock.ONE.turnOff();
        }
    }

}