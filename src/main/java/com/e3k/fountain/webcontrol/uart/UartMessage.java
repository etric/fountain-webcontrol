package com.e3k.fountain.webcontrol.uart;

import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.config.SettingsConfig;
import com.e3k.fountain.webcontrol.config.UmfConfig;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.constant.DeviceType;
import com.e3k.fountain.webcontrol.constant.SettingSlider;
import lombok.NonNull;

import static com.e3k.fountain.webcontrol.CommonUtils.isBitUp;
import static com.e3k.fountain.webcontrol.CommonUtils.setOrUnsetBit;
import static com.e3k.fountain.webcontrol.ValidationUtils.requireInRange;

public enum UartMessage implements Initializable {

  ONE;

  private static final int UMF_DATA_OFFSET = 3;
  private static final int DEVICES_STATE_OFFSET = 5;
  private static final int AUX_BUTTONS_OFFSET = 6;
  private static final int SLIDERS_OFFSET = 14;
  private static final int CURR_PLAYING_ITEM_OFFSET = 18;

  private byte[] uartMessage = defaultUartMessage();

  private static byte[] defaultUartMessage() {
    return new byte[] {
        'M', 'V', 'K', // 0,1,2 - Header
        0, 0, // 3,4 - UMF data
        0, // 5 - fountain, light, sound, filter?, pause
        0, 0, 0, 0, 0, 0, 0, 0, // 6..13 - Aux 64 buttons
        0, 0, 0, 0, // 14,15,16,17 - Motor,RGB
        0, // 18 - currently playing song
        0 // 19 - CRC8
    };
  }

  public DeviceState getDevicesState(DeviceType deviceType) {
    int bitNum = bitByDevice(deviceType);
    boolean bitUp = isBitUp(uartMessage[DEVICES_STATE_OFFSET], bitNum);
    return DeviceState.fromBool(bitUp);
  }

  public /*synchronized*/ void setDeviceState(@NonNull DeviceType deviceType, @NonNull DeviceState deviceState) {
    int bitNum = bitByDevice(deviceType);
    boolean bitUp = deviceState == DeviceState.on;
    uartMessage[DEVICES_STATE_OFFSET] = setOrUnsetBit(uartMessage[DEVICES_STATE_OFFSET], bitNum, bitUp);
  }

  public /*synchronized*/ void setPauseState(boolean pausing) {
    // bit 4 - pause indicator
    uartMessage[DEVICES_STATE_OFFSET] = setOrUnsetBit(uartMessage[DEVICES_STATE_OFFSET], 4, pausing);
  }

  public boolean[] getAuxButtons() {
    boolean[] auxButtons = new boolean[64];
    int ctr = 0;
    for (int byteNum = AUX_BUTTONS_OFFSET; byteNum < AUX_BUTTONS_OFFSET + 8; byteNum++) {
      for (int bitNum = 0; bitNum < 8; bitNum++) {
        auxButtons[ctr++] = isBitUp(uartMessage[byteNum], bitNum);
      }
    }
    return auxButtons;
  }

  public /*synchronized*/ void setAuxButton(int auxButtonIdx, boolean state) {
    requireInRange(auxButtonIdx, 0, 63, "auxButtonIdx");
    int bucket = auxButtonIdx / 8;
    int slot = auxButtonIdx % 8;
    int byteIndex = AUX_BUTTONS_OFFSET + bucket;
    uartMessage[byteIndex] = setOrUnsetBit(uartMessage[byteIndex], slot, state);
    PropertiesManager.ONE.getSettingsConfig().setAuxButton(auxButtonIdx, state);
  }

  public /*synchronized*/ void setUmfBulb(int bulbNum, boolean state) {
    requireInRange(bulbNum, 0, 15, "UMF bulb");
//      appConfig.getUmf().getBulbs().get(bulbNum).setSwitchState(switchState);
    int byteIndex = UMF_DATA_OFFSET + (bulbNum < 8 ? 0 : 1);
    uartMessage[byteIndex] = setOrUnsetBit(uartMessage[byteIndex], bulbNum, state);
  }

  public /*synchronized*/ void setSliderValue(SettingSlider slider, int value) {
    requireInRange(value, 0, 255, "sliderValue");
    byte bytedValue =  (byte) value;
    int byteIndex;
    switch (slider) {
      case motor: byteIndex = 0; break;
      case red: byteIndex = 1; break;
      case green: byteIndex = 2; break;
      case blue: byteIndex = 3; break;
      default: throw new IllegalArgumentException("Unsupported sliderType " + slider);
    }
    uartMessage[SLIDERS_OFFSET + byteIndex] = bytedValue;
    PropertiesManager.ONE.getSettingsConfig().setBySlider(slider, value);
  }

  public /*synchronized*/ void setCurrPlayingItem(int currPlayingItemUserFriendly) {
    requireInRange(currPlayingItemUserFriendly, 0, 25, "currPlayingItem");
    uartMessage[CURR_PLAYING_ITEM_OFFSET] = (byte) currPlayingItemUserFriendly;
    PropertiesManager.ONE.setLastPlayedItem(currPlayingItemUserFriendly);
  }

  public synchronized void calculateCRC8() {
    //TODO
  }

  public byte[] getBytes() {
//    return uartMessage.clone(); //TODO or copy??
    return uartMessage;
  }

  private static int bitByDevice(DeviceType deviceType) {
    switch (deviceType) {
      case fountain: return 0;
      case light: return 1;
      case sound: return 2;
//      case filter: bitNum = 3; break;
      default: throw new IllegalArgumentException("Unsupported deviceType " + deviceType);
    }
  }

  @Override
  public void init() {
    final UmfConfig umfConfig = PropertiesManager.ONE.getUmfConfig();
    final SettingsConfig settingsConfig = PropertiesManager.ONE.getSettingsConfig();

    for (int i = 0; i < 16; i++) {
      boolean setBit = umfConfig.getBulbs().get(i).getSwitchState() == DeviceState.on;
      UartMessage.ONE.setUmfBulb(i, setBit);
    }
    for (int i = 0; i < 64; i++) {
      boolean state = settingsConfig.getAuxButtons()[i];
      UartMessage.ONE.setAuxButton(i, state);
    }
    for (SettingSlider slider : SettingSlider.values()) {
      UartMessage.ONE.setSliderValue(slider, settingsConfig.getBySlider(slider));
    }
  }
}
