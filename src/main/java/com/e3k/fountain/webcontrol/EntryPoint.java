package com.e3k.fountain.webcontrol;

import com.e3k.fountain.webcontrol.ui.MainFrame;
import com.e3k.fountain.webcontrol.web.WebServer;

import java.awt.*;

import static javax.swing.UIManager.*;

public class EntryPoint {

    public static void main(String args[]) {
        if (args != null && args.length > 0) {
            Static.D = args[0].toLowerCase().equals("d");
        }

//        try {
//            setLookAndFeel(getSystemLookAndFeelClassName());
//        } catch (Exception ignored) {}
//
//        // start desktop UI
//        EventQueue.invokeLater(() -> new MainFrame().setVisible(true));

        // start web server
        WebServer.bootstrap();
    }

}