package com.e3k.fountain.webcontrol.config;

import lombok.Getter;

@Getter
public class UmfSmsConfig {

    private boolean disabled = true;
    private String username = "";
    private String password = "";
    private String alphaName = "";
    private String recipients = "";
}
