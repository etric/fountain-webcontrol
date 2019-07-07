package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
class UmfConfig {

    private int baudRate = 38400;
    private int dataBits = 8;
    private String parity = "NONE";
    private int stopBits = 1;
    private String flowControl = "NONE";
    private String password = "1234";
    private UmfEmailConfig email = new UmfEmailConfig();
    private List<UmfBulbConfig> bulbs = defaultBulbsConfig();

    private static List<UmfBulbConfig> defaultBulbsConfig() {
        return IntStream.range(1, 17).boxed()
                .map(i -> new UmfBulbConfig(DeviceState.off, "Лампа " + i, false))
                .collect(Collectors.toList());
    }

}
