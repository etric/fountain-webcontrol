package com.e3k.fountain.webcontrol.sysdatetime;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;

@Slf4j
public enum SysDateTimeManager {

    ONE;

    private static final DateTimeFormatter sysDateCmdPattern =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(ISO_LOCAL_DATE)
                    .appendLiteral(' ')
//                    .append(ISO_LOCAL_TIME)
                    .appendValue(HOUR_OF_DAY, 2)
                    .appendLiteral(':')
                    .appendValue(MINUTE_OF_HOUR, 2)
                    .toFormatter();

    public void parseAndUpdateTime(String localDateTimeRaw) throws IOException, InterruptedException {
        //ISO-8601 calendar system -- 2007-12-03T10:15:30
        log.info("Updating System Date & Time: {} --> {}", LocalDateTime.now(), localDateTimeRaw);
        LocalDateTime.parse(localDateTimeRaw, sysDateCmdPattern); //validation

        Process process = Runtime.getRuntime().exec("sudo date -s '" + localDateTimeRaw + "'");
        process.waitFor();

        //date -s '2009-02-13 11:31:30' #that's a magical timestamp
    }

    public String getTimeFormatted() {
        return sysDateCmdPattern.format(LocalDateTime.now());
    }
}
