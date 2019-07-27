package com.e3k.fountain.webcontrol.notification;

import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.BulbState;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public enum NotificationSender implements Initializable {

    ONE;

    private String subject;

    @Override
    public synchronized void init() throws Exception {
        if (subject == null) {
            EmailSender.ONE.init();
            SmsSender.ONE.init();
            subject = "УМФ - " + PropertiesManager.ONE.getObjectName();
        }
    }

    public void sendNotification(Map<String, BulbState> changedBulbsData) {
        final String message = buildMessageText(changedBulbsData);
        EmailSender.ONE.sendEmailAsync(subject, message);
        SmsSender.ONE.sendSms(subject, message);
    }

    private static String buildMessageText(Map<String, BulbState> changedBulbsStates) {
        final StringBuilder msgBuilder = new StringBuilder();
        for (Map.Entry<String, BulbState> entry : changedBulbsStates.entrySet()) {
            final String bulbStateText = (entry.getValue() == BulbState.red) ? "ТРЕВОГА" : "НОРМА";
            msgBuilder.append(entry.getKey()).append(" - ").append(bulbStateText).append("\n");
        }
        return msgBuilder.deleteCharAt(msgBuilder.length() - 1).toString();
    }
}
