package com.example.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigApp {

    private static ConfigApp config = new ConfigApp();
    private String ipAddress;
    private int port;

    public static String getIpAddress() {
        checkAllParameters();
        return config.ipAddress;
    }

    public static int getPort() {
        checkAllParameters();
        return config.port;
    }

    public static void checkAllParameters() {
        if (config.ipAddress == null || config.port == 0) {
            readConfig();
        }
    }

    private static void readConfig() {
        try (InputStream in = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            config.ipAddress = properties.getProperty("ip.address");
            config.port = Integer.parseInt(properties.getProperty("port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
