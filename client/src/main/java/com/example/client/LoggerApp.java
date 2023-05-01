package com.example.client;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoggerApp {

    private static Logger logger;

    private static void checkExistsLoggerAndCreate(){
        if (logger == null){
            logger = Logger.getLogger(ClientApp.class);
            BasicConfigurator.configure();
        }
    }

    public static Logger getLogger() {
        checkExistsLoggerAndCreate();
        return logger;
    }

    public static void info(String msg){
        checkExistsLoggerAndCreate();
        logger.info(msg);
    }

    public static void warn(String msg){
        checkExistsLoggerAndCreate();
        logger.warn(msg);
    }
}
