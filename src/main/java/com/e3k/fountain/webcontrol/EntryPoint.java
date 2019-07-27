package com.e3k.fountain.webcontrol;

import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.io.PinJokey;
import com.e3k.fountain.webcontrol.io.SoundExtCtrlDevice;
import com.e3k.fountain.webcontrol.io.UartDevice;
import com.e3k.fountain.webcontrol.notification.NotificationSender;
import com.e3k.fountain.webcontrol.web.WebServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class EntryPoint {

    //ORDER IS IMPORTANT !!!
    //ORDER IS IMPORTANT !!!
    //ORDER IS IMPORTANT !!!
    private static final List<Initializable> initializables = Arrays.asList(
            PropertiesManager.ONE,
            PinJokey.ONE,
            NotificationSender.ONE,
            UartDevice.ONE,
            AlarmClock.ONE,
            SoundExtCtrlDevice.ONE
    );

    public static void main(String args[]) throws Exception {
        try {
            for (Initializable item : initializables) {
                item.init();
            }
            // start web server
            WebServer.bootstrap();
        }
        catch (Exception ex) {
            log.error("Application start failed", ex);
            throw ex;
        }
    }

}
