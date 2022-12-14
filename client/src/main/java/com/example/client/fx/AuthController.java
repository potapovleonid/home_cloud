package com.example.client.fx;

import com.example.client.network.Network;
import com.example.client.network.networking.RequestAuthorize;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

public class AuthController {

    @FXML
    public TextField loginField;

    @FXML
    public TextField passwordField;

    public void sendAuthCredential(ActionEvent actionEvent) {
        if (loginField.getText() == null || passwordField.getText() == null){
            new Alert(Alert.AlertType.WARNING, "Someone field is empty, please try again", ButtonType.OK);
        }
        Network.getNetwork().getChannel().writeAndFlush(new RequestAuthorize(loginField.getText(), passwordField.getText()));
    }


}
