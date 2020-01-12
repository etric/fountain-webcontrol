package com.e3k.fountain.webcontrol.config;

import com.e3k.fountain.webcontrol.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.e3k.fountain.webcontrol.config.ConfigHasher.*;
import static com.e3k.fountain.webcontrol.config.ConfigUtils.buildGson;
import static com.e3k.fountain.webcontrol.config.ConfigValidator.validate;

enum ConfigIO {

  ONE;

  private static final TimeUnit INTERVAL_TIME_UNIT = TimeUnit.SECONDS;
  private static final int INTERVAL_PERIOD = 20;

  private final ScheduledExecutorService configSaverExecutor = Executors.newSingleThreadScheduledExecutor();
  private final AtomicInteger configLastHash = new AtomicInteger(-1);
  private final Gson gson = buildGson();
  private final File configFile = new File("config.json");
  private AppConfig appConfig;

  synchronized AppConfig loadConfig() {
    if (this.appConfig != null) {
      throw new IllegalStateException("Config is already loaded!");
    }
    loadConfig0();
    logger().info("Validating Config...");
    validate(this.appConfig);
    recalculateConfigHash();
    startConfigSaverExecutor();
    return appConfig;
  }

  String dumpConfig() {
    JsonObject jsonObject = (JsonObject) gson.toJsonTree(appConfig);
    jsonObject.addProperty("version", CommonUtils.getAppVersion());
    return jsonObject.toString();
  }

  private boolean recalculateConfigHash() {
//    logger().debug("Recalculating Config hash...");
    int newHash = Objects.hash(
        appConfig.getControlMode().name(),
        appConfig.getPauseBetweenTracks(),
        appConfig.getLastPlayedItem(),
        appConfig.getVolume(),
        umfConfigHash(appConfig.getUmf()),
        settingsConfigHash(appConfig.getSettings()),
        devicesConfigHash(appConfig.getDevices())
    );
    int oldHash = configLastHash.getAndSet(newHash);
    logger().debug("Config OLD hash = {}, NEW hash = {}", oldHash, newHash);
    return oldHash != newHash;
  }

  private void startConfigSaverExecutor() {
    configSaverExecutor.scheduleAtFixedRate(() -> {
      if (recalculateConfigHash()) {
        logger().info("Changes found. Storing Config...");
        storeConfig();
      }
    }, INTERVAL_PERIOD, INTERVAL_PERIOD, INTERVAL_TIME_UNIT);
  }

  private void storeConfig() {
    try (Writer propsOut = new FileWriter(configFile)) {
      gson.toJson(appConfig, propsOut);
      logger().debug("Properties successfully stored");
    } catch (IOException ex) {
      logger().error("Failed storing properties", ex);
    }
  }

  private void loadConfig0() {
    logger().info("Loading config from {}", configFile.getName());
    if (configFile.exists()) {
      try (Reader reader = Files.newBufferedReader(configFile.toPath(), Charset.forName("UTF-8"))) {
        appConfig = gson.fromJson(reader, AppConfig.class);
        if (appConfig != null) {
          logger().info("Config successfully loaded");
          return;
        }
      } catch (IOException ex) {
        logger().error("Failed loading config from file", ex);
      }
    } else {
      try {
        configFile.createNewFile();
      } catch (IOException ex) {
        logger().error("Failed creating config file", ex);
      }
    }
    logger().warn("Storing default config as a template");
    appConfig = new AppConfig();
    storeConfig();
  }

  private static Logger logger() {
    return LoggerFactory.getLogger(ConfigIO.class);
  }
}
