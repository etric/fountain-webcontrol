package com.e3k.fountain.webcontrol;

import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.io.SoundExtCtrlDevice;
import com.e3k.fountain.webcontrol.uart.UartDevice;
import com.e3k.fountain.webcontrol.notification.NotificationSender;
import com.e3k.fountain.webcontrol.uart.UartMessage;
import com.e3k.fountain.webcontrol.web.WebServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntryPoint {

    public static void main(String args[]) throws Exception {
        try {
            //ORDER IS IMPORTANT !!!
            PropertiesManager.ONE.init();
            UartMessage.ONE.init();
            NotificationSender.ONE.init();
            UartDevice.ONE.init();
            AlarmClock.ONE.init();

//            if (PropertiesManager.ONE.isSoundDevicesEnabled()) {
//                SoundExtCtrlDevice.ONE.init();
//            }

            // start web server
            WebServer.bootstrap();
        }
        catch (Exception ex) {
            log.error("Application start failed", ex);
            throw ex;
        }
    }
}
