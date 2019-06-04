package com.e3k.fountain.webcontrol.sysdatetime;

import lombok.extern.slf4j.Slf4j;
import spark.utils.Assert;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

@Slf4j
public enum SysDateTimeManager {

    ONE;

    //ISO-8601 calendar system -- 2007-12-03T10:15:30
    private static final DateTimeFormatter sysDateCmdPattern =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(ISO_LOCAL_DATE)
                    .appendLiteral(' ')
                    .appendValue(HOUR_OF_DAY, 2)
                    .appendLiteral(':')
                    .appendValue(MINUTE_OF_HOUR, 2)
                    .toFormatter();

    public void parseAndUpdateTime(String localDateTimeRaw) throws IOException, InterruptedException {
        LocalDateTime parsedNewDateTime = LocalDateTime.parse(localDateTimeRaw, sysDateCmdPattern); //validation

        log.info("Updating System Date & Time: {} --> {}", LocalDateTime.now(), localDateTimeRaw);

        Process process = Runtime.getRuntime().exec(new String[] {"date", "-s", localDateTimeRaw});
        process.waitFor();

        checkIsEqualToNowUpToMinutes(parsedNewDateTime);
    }

    public String getTimeFormatted() {
        return sysDateCmdPattern.format(LocalDateTime.now());
    }

    private void checkIsEqualToNowUpToMinutes(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        String error = "Date was not updated (correctly)!";
        Assert.isTrue(now.getYear() == dateTime.getYear(), error);
        Assert.isTrue(now.getMonth() == dateTime.getMonth(), error);
        Assert.isTrue(now.getDayOfMonth() == dateTime.getDayOfMonth(), error);
        Assert.isTrue(now.getHour() == dateTime.getHour(), error);
        Assert.isTrue(now.getMinute() == dateTime.getMinute(), error);
    }
}
