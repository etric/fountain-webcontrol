package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.constant.DeviceState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UmfBulbConfig {

    @Setter(AccessLevel.PACKAGE)
    private DeviceState switchState;

    private String label;
    private boolean sendEmail;

}
