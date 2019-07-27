package com.e3k.fountain.webcontrol.notification;

import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
enum EmailSender implements Initializable {

    ONE;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicLong msgCounter = new AtomicLong(0);

    private Session session;
    private Address sender;
    private Address[] recipients;

    @Override
    public synchronized void init() {
        if (session == null) {
            sender = PropertiesManager.ONE.getUmfEmailSender();
            recipients = PropertiesManager.ONE.getUmfEmailRecipients();

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            PropertiesManager.ONE.getUmfEmailUsername(), PropertiesManager.ONE.getUmfEmailPassword());
                }
            });
        }
    }

    public void sendEmailAsync(String subject, String message) {
        executor.execute(() -> sendEmail(subject, message));
    }

    private void sendEmail(String subject, String msgText) {
        final long msgNum = msgCounter.incrementAndGet();

        log.info("Sending #{} email", msgNum);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(sender);
            message.addRecipients(Message.RecipientType.TO, recipients);
            message.setSubject(subject);
            message.setText(msgText);

            Transport.send(message);
            log.info("Email #{} sent", msgNum);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.warn("Failed waiting after sending email");
            }
        } catch (MessagingException e) {
            log.error("Failed sending #" + msgNum + "email message", e);
        }
    }
}
