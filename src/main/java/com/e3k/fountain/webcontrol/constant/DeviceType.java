package com.e3k.fountain.webcontrol.constant;

public enum DeviceType {
    fountain, light,
    sound, soundFreqGen, soundIndicator, soundExtCtrl,
    auxGpio1, auxGpio2, auxGpio3, auxGpio4, auxGpio5, auxGpio6;

    public boolean isSoundRelated() {
        return this == sound || this == soundFreqGen || this == soundIndicator || this == soundExtCtrl;
    }
}
