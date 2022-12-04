package com.example.client.network.handlers;

import com.example.client.LoggerApp;
import com.example.client.fx.Controller;

public class AppControllers {

    private static Controller controller;

    public static void setController(Controller controller) {
        LoggerApp.info("CONTROLLER SET: " + controller.toString());
        if (controller != null) {
            AppControllers.controller = controller;
        } else {
            throw new RuntimeException("Controller already set");
        }
    }

    public static Controller getController() {
        return controller;
    }
}
