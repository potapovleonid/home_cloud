package com.example.client;

import com.example.client.network.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXController extends Application {

    private Scene mainScene;
    private Scene authScene;

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("authPanel.fxml"));
        authScene = new Scene(loader.load());

        loader = new FXMLLoader(ClientApp.class.getResource("main.fxml"));
        mainScene = new Scene(loader.load(), 1200, 800);

        this.stage.setTitle("Home cloud Authorizing");
        this.stage.setScene(authScene);

        Network.getNetwork().setCallbackAuthenticated(resultAuth -> {
            if (resultAuth) {
                LoggerApp.info("Auth success, delete auth pipeline");
                this.stage.setScene(mainScene);
                this.stage.setTitle("Home cloud");
            } else {
                LoggerApp.info("Please try authenticate again");
            }
        });

        stage.show();
    }

    public static void startFX(){
        launch();
    }



}
