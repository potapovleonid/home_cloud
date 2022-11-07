package com.example.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoggerApp {

    private static org.apache.log4j.Logger logger;

    public static Logger getLogger() {
        if (logger == null){
            logger = Logger.getLogger(ServerApp.class);
            BasicConfigurator.configure();
        }
        return logger;
    }

    public static void info(String msg){
        logger.info(msg);
    }
}
