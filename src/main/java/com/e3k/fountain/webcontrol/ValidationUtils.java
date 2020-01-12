package com.e3k.fountain.webcontrol;

import lombok.experimental.UtilityClass;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Objects;

@UtilityClass
public class ValidationUtils {

  public static void requireInRange(int target, int min, int max, String msg) {
    if (target < min || target > max) {
      throw new IllegalArgumentException(msg + ": " + target +
          " is not in range [" + min + ".." + max + "]");
    }
  }

  public static void requireState(boolean state, String msg) {
    if (!state) {
      throw new IllegalArgumentException(msg);
    }
  }

  public static void requireAddress(String address, String msg) {
    Objects.requireNonNull(address, msg);
    try {
      new InternetAddress(address);
    } catch (AddressException e) {
      throw new IllegalArgumentException(msg + ": " + address + " has invalid address");
    }
  }

  public static void requireUaPhones(String phones, String msg) {
    Objects.requireNonNull(phones, msg);
    for (String phone : phones.split("[,;]")) {
      if (!phone.matches("380\\d{9}")) {
        throw new IllegalArgumentException(msg + ": " + phone + " - invalid UA phone number(s)");
      }
    }
  }

}
