package com.example.client;

import com.example.client.fx.AuthController;
import com.example.client.fx.PanelController;
import com.example.client.network.Network;
import com.example.client.network.RequestAuthorize;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ClientApp {

    public static void main(String[] args) {
        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            new Thread(() -> Network.getNetwork().start(countDownNetworkConnections)).start();
            countDownNetworkConnections.await();
            FXController.startFX();
//            Network.getNetwork().getChannel().writeAndFlush(new RequestAuthorize("des", "des123"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
