package com.example.client.fx;

import com.example.client.network.Network;
import com.example.client.network.networking.RequestChangePassword;
import com.example.client.network.networking.RequestList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.util.Objects;

public class ChangePasswordController {
    @FXML
    public TextField oldPassword;
    @FXML
    public TextField newPassword;

    public void btnChangePassword(ActionEvent actionEvent) {
        if (Objects.equals(oldPassword.getText(), "") || Objects.equals(newPassword.getText(), "")){
            JOptionPane.showMessageDialog(null, "Someone field is empty, please try again",
                    "Empty field", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
//        TODO check length, check equals new and old passwords
        Platform.runLater(() -> Network.getNetwork().getChannel().writeAndFlush(new RequestChangePassword(oldPassword.getText(), newPassword.getText())));
    }

    public void btnCancelChangePassword(ActionEvent actionEvent) {
        ReplaceSceneManager.replaceSceneContent("main.fxml", "Home cloud");
        Platform.runLater(() -> Network.getNetwork().getChannel().writeAndFlush(new RequestList()));
    }
}
