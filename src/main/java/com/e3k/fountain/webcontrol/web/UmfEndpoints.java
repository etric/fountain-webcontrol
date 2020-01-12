package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.constant.DeviceState;
import com.e3k.fountain.webcontrol.uart.UartDevice;
import spark.Route;

import static spark.Spark.get;
import static spark.Spark.put;

final class UmfEndpoints {

  static void init() {

    // Page
    final Route pageRoute = (request, response) ->
        WebServer.class.getClassLoader().getResourceAsStream("umf.html");

    get("umf-unsafe", pageRoute);
    get("api/umf/page", umfAuthenticated(pageRoute));

    put("api/umf/bulb/:bulbNum/:switchState", (request, response) -> {
      final int bulbNum = Integer.parseInt(request.params(":bulbNum"));
      final DeviceState bulbState = DeviceState.valueOf(request.params(":switchState"));
      PropertiesManager.ONE.setUmfBulbSwitchState(bulbNum, bulbState);
      return "OK";
    });
    // for initial load
    get("api/umf/bulb/list", (request, response) ->
        PropertiesManager.ONE.getAllUmfBulbInfo(), JsonResponseTransformer.ONE);
    // for polling
    get("api/umf/bulb/states", (request, response) ->
        UartDevice.ONE.getBulbStates(), JsonResponseTransformer.ONE);

  }

  private static Route umfAuthenticated(Route route) {
    return (request, response) -> {
      final String pswd = request.headers("pswd");
      if (pswd == null || !pswd.equals(PropertiesManager.ONE.getUmfPassword())) {
        response.status(403);
        return "Неверный пароль!";
      } else {
        return route.handle(request, response);
      }
    };
  }
}
