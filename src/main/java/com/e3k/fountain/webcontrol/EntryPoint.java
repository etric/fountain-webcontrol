package com.e3k.fountain.webcontrol;

import com.e3k.fountain.webcontrol.alarm.AlarmClock;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.email.EmailSender;
import com.e3k.fountain.webcontrol.io.PinJokey;
import com.e3k.fountain.webcontrol.io.UartDevice;
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
            EmailSender.ONE,
            UartDevice.ONE,
            AlarmClock.ONE
    );

    public static void main(String args[]) throws Exception {

        for (Initializable item : initializables) {
            item.init();
        }

        // start web server
        WebServer.bootstrap();

    }

}
