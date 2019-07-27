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

    private String switchLabel;
    private String bulbLabel;
    private boolean notifyOnChange;

    private volatile String fullLabel = null;

    public String getFullLabel() {
        if (fullLabel == null) {
            synchronized (this) {
                if (fullLabel == null) {
                    fullLabel = switchLabel + "/" + bulbLabel;
                }
            }
        }
        return fullLabel;
    }
}
