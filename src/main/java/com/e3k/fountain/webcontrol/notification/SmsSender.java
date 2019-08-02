package com.e3k.fountain.webcontrol.notification;

import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.config.UmfSmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
enum SmsSender implements Initializable {

    ONE;

    private static final String SMS_SERVICE_URL = "https://gate.smsclub.mobi/xml/";
    private final HttpClient httpClient = new HttpClient(new SslContextFactory());
    private final AtomicLong smsCounter = new AtomicLong(0);

    private final Thread stopHttpClientHook = new Thread(() -> {
        Logger _log = LoggerFactory.getLogger(SmsSender.class);
        _log.info("Stopping HttpClient...");
        try {
            httpClient.stop();
        } catch (Exception e) {
            _log.error("Failed stopping HttpClient", e);
        }
    });

    private String username;
    private String password;
    private String alphaName;
    private String recipients;

    @Override
    public synchronized void init() throws Exception {
        if (isDisabled()) {
            return;
        }
        if (httpClient.isStopping() || httpClient.isStopped()) {
            log.info("Starting HttpClient...");
            httpClient.start();
            Runtime.getRuntime().addShutdownHook(stopHttpClientHook);

            final UmfSmsConfig smsConfig = PropertiesManager.ONE.getUmfSmsConfig();
            username = smsConfig.getUsername();
            password = smsConfig.getPassword();
            alphaName = smsConfig.getAlphaName();
            recipients = smsConfig.getRecipients().replaceAll(",", ";");
        }
    }

    public void sendSms(String subject, String message) {
        if (isDisabled()) {
            return;
        }
        final long smsNum = smsCounter.incrementAndGet();
        final String titledMessage = subject + ":\n" + message;
        final String payload =
                "<?xml version='1.0' encoding='utf-8'?>" +
                "<request_sendsms>" +
                "      <username><![CDATA[" + username + "]]></username>" +
                "      <password><![CDATA[" + password + "]]></password>" +
                "      <from><![CDATA[" + alphaName + "]]></from>" +
                "      <to><![CDATA[" + recipients + "]]></to>" +
                "      <text><![CDATA[" + titledMessage + "]]></text>" +
                "</request_sendsms>";

        log.debug("Sending SMS #{}: {}", smsNum, titledMessage);
        httpClient.POST(SMS_SERVICE_URL)
                .content(new StringContentProvider(payload))
                .send(result -> {
                    if (result.isSucceeded() && result.getResponse().getStatus() == HttpStatus.OK_200) {
                        log.debug("SMS #{} sent", smsNum);
                    } else if (result.isFailed()) {
                        log.error("SMS #{} NOT sent: {}", smsNum, result.getFailure().getMessage());
                    }
                });
    }

    private boolean isDisabled() {
        return PropertiesManager.ONE.getUmfSmsConfig().isDisabled();
    }
}
