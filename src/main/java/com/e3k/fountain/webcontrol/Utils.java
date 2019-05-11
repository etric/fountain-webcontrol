package com.e3k.fountain.webcontrol;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public class Utils {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String timeToString(LocalTime time) {
        return dateTimeFormatter.format(time);
    }

    public static LocalTime stringToTime(String str) {
        return LocalTime.parse(str, dateTimeFormatter);
    }

//    public static int[] parseData(String stringData) {
//        StringTokenizer st = new StringTokenizer(stringData);
//        int[] data = new int[st.countTokens()];
//        int i = 0;
//        while (st.hasMoreElements()) {
//            int nextElem = Integer.parseInt((String) st.nextElement());
//            data[i++] = Math.min(255, Math.abs(nextElem));
//        }
//        return data;
//    }
//
//
//    public static String formatExceptionMessage(Exception e) {
//        return "";
//    }
//
//    public static void trySleep(int i) {
//    }
//
//    public static void error(SerialPortException spe) {
//    }
}