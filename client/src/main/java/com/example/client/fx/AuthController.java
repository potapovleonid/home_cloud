package com.example.client.fx;

import com.example.client.network.Network;
import com.example.client.network.networking.RequestAuthorize;
import com.example.client.network.networking.RequestRegisterUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.util.Objects;

public class AuthController {

    @FXML
    public TextField loginField;

    @FXML
    public TextField passwordField;

    public void sendAuthCredential(ActionEvent actionEvent) {
        if (Objects.equals(loginField.getText(), "") || Objects.equals(passwordField.getText(), "")){
            JOptionPane.showMessageDialog(null, "Someone field is empty, please try again",
                    "Empty field", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Network.getNetwork().getChannel().writeAndFlush(new RequestAuthorize(loginField.getText(), passwordField.getText()));
    }

    public void sendRegisterCredentials(ActionEvent actionEvent) {
        if (Objects.equals(loginField.getText(), "") || Objects.equals(passwordField.getText(), "")){
            JOptionPane.showMessageDialog(null, "Someone field is empty, please try again",
                    "Empty field", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Network.getNetwork().getChannel().writeAndFlush(new RequestRegisterUser(loginField.getText(), passwordField.getText()));
    }

}
