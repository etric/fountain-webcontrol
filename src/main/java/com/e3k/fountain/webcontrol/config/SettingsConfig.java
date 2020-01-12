package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.constant.SettingSlider;
import lombok.Getter;

@Getter
public class SettingsConfig {

  private boolean[] auxButtons = new boolean[64];
  private int motor = 0;
  private int red = 0;
  private int green = 0;
  private int blue = 0;

  public void setAuxButton(int auxIdx, boolean state) {
    this.auxButtons[auxIdx] = state;
  }

  public void setBySlider(SettingSlider slider, int byteValue) {
    switch (slider) {
      case motor:
        motor = byteValue;
        return;
      case red:
        red = byteValue;
        return;
      case green:
        green = byteValue;
        return;
      case blue:
        blue = byteValue;
    }
  }

  public int getBySlider(SettingSlider slider) {
    switch (slider) {
      case motor:
        return motor;
      case red:
        return red;
      case green:
        return green;
      case blue:
        return blue;
      default:
        throw new IllegalArgumentException("Unsupported slider " + slider);
    }
  }

}
