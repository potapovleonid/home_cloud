package com.example.client.network.handlers;

import com.example.client.LoggerApp;
import com.example.client.fx.Controller;

public class AppControllers {

    private static Controller controller;

    public static void setController(Controller controller) {
        LoggerApp.info("CONTROLLER SET: " + controller.toString());
        AppControllers.controller = controller;
    }

    public static Controller getController() {
        return controller;
    }
}
