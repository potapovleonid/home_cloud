package com.example.client;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoggerApp {

    private static Logger logger;

    public static Logger getLogger() {
        if (logger == null){
            logger = Logger.getLogger(ClientApp.class);
            BasicConfigurator.configure();
        }
        return logger;
    }

    public static void info(String msg){
        logger.info(msg);
    }
}
