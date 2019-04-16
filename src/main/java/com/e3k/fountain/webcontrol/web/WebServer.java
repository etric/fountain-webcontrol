package com.e3k.fountain.webcontrol.web;

import static spark.Spark.*;

public class WebServer {

    public static void bootstrap() {
        ipAddress("localhost");
        port(9090);

        String projectDir = System.getProperty("user.dir");
        String staticDir = "/src/main/web-resources";
        staticFiles.externalLocation(projectDir + staticDir);

//        staticFileLocation("/web-resources");
        new WebResource();
    }
}
