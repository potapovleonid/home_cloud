package com.example.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoggerApp {

    private static org.apache.log4j.Logger logger;

    public static void init(){
        logger = Logger.getLogger(ServerApp.class);
        BasicConfigurator.configure();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void addInfo(String msg){
        logger.info(msg);
    }
}
