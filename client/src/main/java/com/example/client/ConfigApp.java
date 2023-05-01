package com.example.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigApp {

    private static ConfigApp config;
    private String ipAddress;
    private int port;

    public ConfigApp() {
        readConfig();
    }

    public static ConfigApp getConfig(){
        if (config == null){
            config = new ConfigApp();
        }
        return config;
    }

    public String getIpAddress() {
        assert config.ipAddress != null : "IP address is null into config";
        return config.ipAddress;
    }

    public int getPort() {
        assert config.port != 0 : "Port address is null into config";
        return config.port;
    }

    private void readConfig() {
        try (InputStream in = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            ipAddress = properties.getProperty("ip.address");
            port = Integer.parseInt(properties.getProperty("port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
