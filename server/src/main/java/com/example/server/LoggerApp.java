package com.example.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoggerApp {

    private static org.apache.log4j.Logger logger;

    private static void checkExistsLoggerAndCreate(){
        if (logger == null){
            logger = Logger.getLogger(ServerApp.class);
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
}
