package com.e3k.fountain.webcontrol.web;

import javax.servlet.MultipartConfigElement;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Random;

import static spark.Spark.*;

public class WebResource {

//    private static final String JSON_ACCEPT_TYPE = "application/json";

    public WebResource() {
        setupEndpoints();
    }

    private void setupEndpoints() {

        put("/api/music/:musicNum", (request, response) -> {
            int musicNum = Integer.parseInt(request.params(":musicNum"));
            if (musicNum < 1 || musicNum > 20) {
                response.status(400);
                return response;
            }

            request.attribute("org.eclipse.jetty.multipartConfig",
                    new MultipartConfigElement("/temp"));

            File musicDir = new File(System.getProperty("user.dir"), "music");
            musicDir.mkdir();
            File musicNumDir = new File(musicDir, String.valueOf(musicNum));
            musicNumDir.mkdir();

            final String fileName = request.raw().getPart("file").getSubmittedFileName();
            String fileFullPath = System.getProperty("user.dir") + "/music/" + musicNum + "/" + fileName;
            Path musicFile = Files.createFile(Paths.get(fileFullPath));

            System.out.println("Uploading fileName " + fileName);

            try (InputStream input = request.raw().getPart("file").getInputStream()) {
                Files.copy(input, musicFile, StandardCopyOption.REPLACE_EXISTING);
            }

            final File[] musicOldFiles = musicNumDir.listFiles((dir, name) -> !name.equals(fileName));
            if (musicOldFiles != null) {
                Arrays.stream(musicOldFiles).forEach(File::delete);
            }

            response.status(200);
            return response;
        });

        put("/api/mode/:modeAuto", (request, response) -> {
            final ControlMode mode = ControlMode.valueOf(request.params(":modeAuto").toLowerCase());
            if (ControlMode.auto == mode) {
                System.out.println("Switching to AUTO mode");
            } else {
                System.out.println("Switching to MANUAL mode");
            }
            response.status(200);
            return response;
        });

        put("/api/volume/:val", (request, response) -> {
            final int val = Integer.valueOf(request.params(":val"));
            System.out.println("Updating volume: " + val);
            response.status(200);
            return response;
        });

        put("/api/alarm/:alarmName", (request, response) -> {
            System.out.println("Updating alarm "
                    + request.params(":alarmName") + ": " + request.body());
            response.status(200);
            return response;
        });

        put("/api/:deviceType/:modeOn", (request, response) -> {
            final DeviceType deviceType = DeviceType.valueOf(request.params(":deviceType").toLowerCase());
            final DeviceSwitch deviceSwitch = DeviceSwitch.valueOf(request.params(":modeOn").toLowerCase());
            if (DeviceSwitch.on == deviceSwitch) {
                System.out.println("Switching " + deviceType + " ON");
            } else {
                System.out.println("Switching " + deviceType + " OFF");
            }
            response.status(200);
            return response;
        });
    }

    enum ControlMode {
        auto, manual
    }

    enum DeviceSwitch {
        on, off
    }

    enum DeviceType {
        fountain, light, sound
    }
}
