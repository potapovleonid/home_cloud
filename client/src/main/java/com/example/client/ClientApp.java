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

public class ClientApp extends Application {

    private Scene mainScene;
    private Scene authScene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("authPanel.fxml"));
        authScene = new Scene(loader.load());

        loader = new FXMLLoader(ClientApp.class.getResource("main.fxml"));
        mainScene = new Scene(loader.load(), 1200, 800);

        stage.setTitle("Home cloud Authorizing");
        stage.setScene(authScene);

        Network.getNetwork().setCallbackAuthenticated(resultAuth -> {
            if (resultAuth) {
                LoggerApp.info("Auth success, delete auth pipeline");
                stage.setTitle("Home cloud");
                stage.setScene(mainScene);
            } else {
                LoggerApp.info("Please try authenticate again");
                new Alert(Alert.AlertType.INFORMATION, "Invalid Login or password", ButtonType.OK).show();
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            new Thread(() -> Network.getNetwork().start(countDownNetworkConnections)).start();
            countDownNetworkConnections.await();
            launch();
//            Network.getNetwork().getChannel().writeAndFlush(new RequestAuthorize("des", "des123"));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
