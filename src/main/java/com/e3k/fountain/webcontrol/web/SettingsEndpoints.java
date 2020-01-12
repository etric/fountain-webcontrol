package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.SettingSlider;
import com.e3k.fountain.webcontrol.uart.UartMessage;
import spark.Route;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static spark.Spark.get;
import static spark.Spark.put;

final class SettingsEndpoints {

  private static final BigDecimal BIG_DECIMAL_255 = BigDecimal.valueOf(255);
  private static final BigDecimal BIG_DECIMAL_100 = BigDecimal.valueOf(100);

  static void init() {

    // Page
    final Route pageRoute = (request, response) ->
        WebServer.class.getClassLoader().getResourceAsStream("settings.html");

    get("settings-unsafe", pageRoute);
    get("api/settings/page", pageRoute);

    auxButtonEndpoints();
    sliderEndpoints();
  }

  private static void auxButtonEndpoints() {
    get("/api/settings/auxButtons", (request, response) -> {
      response.status(200);
      return PropertiesManager.ONE.getSettingsConfig().getAuxButtons();
    }, JsonResponseTransformer.ONE);
    put("/api/settings/auxButtons/:idx/:state", (request, response) -> {
      final int auxIdx = Integer.valueOf(request.params(":idx"));
      final boolean state = Boolean.valueOf(request.params(":state"));
      if (auxIdx < 0 || auxIdx > 63) {
        response.status(400);
        return "Aux Button idx must be within range 0..63";
      }
      UartMessage.ONE.setAuxButton(auxIdx, state);
      response.status(200);
      return "OK";
    });
  }

  private static void sliderEndpoints() {
    get("/api/settings/sliders/:slider", (request, response) -> {
      SettingSlider slider = SettingSlider.valueOf(request.params(":slider"));
      int byteValue = PropertiesManager.ONE.getSettingsConfig().getBySlider(slider);
      response.status(200);
      return BigDecimal.valueOf(byteValue * 100)
          .divide(BIG_DECIMAL_255, RoundingMode.HALF_UP)
          .intValue();
    });
    put("/api/settings/sliders/:slider/:val", (request, response) -> {
      SettingSlider slider = SettingSlider.valueOf(request.params(":slider"));
      int sliderPercent = Integer.valueOf(request.params(":val"));
      if (sliderPercent < 1 || sliderPercent > 100) {
        response.status(400);
        return slider + " value must be within range 1..100";
      }
      int byteValue = BigDecimal.valueOf(sliderPercent * 255)
          .divide(BIG_DECIMAL_100, RoundingMode.HALF_UP)
          .intValue();
      UartMessage.ONE.setSliderValue(slider, byteValue);
      response.status(200);
      return "OK";
    });
  }
}
