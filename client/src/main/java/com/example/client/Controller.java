package com.example.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class Controller {


    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

}
