package com.example.client;

import com.example.client.fx.Controller;
import com.example.client.fx.ReplaceSceneManager;
import com.example.client.network.Network;
import com.example.client.network.networking.RequestList;
import com.example.client.network.handlers.AppControllers;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXController extends Application {

    private Scene authScene;

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        ReplaceSceneManager.setStage(stage);

        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("authPanel.fxml"));
        authScene = new Scene(loader.load());

        this.stage.setTitle("Home cloud Authorizing");
        this.stage.setScene(authScene);

        Network.getNetwork().setCallbackAuthenticated(resultAuth -> {
            if (resultAuth) {
                LoggerApp.info("Auth success");
                ReplaceSceneManager.replaceSceneContent("main.fxml", "Home cloud");
                Platform.runLater(() -> {
                    Network.getNetwork().getChannel().writeAndFlush(new RequestList());
                });
            } else {
                LoggerApp.info("Please try authenticate again");
            }
        });

        Network.getNetwork().setCallbackGettingFileList(files -> {
            Controller controller = AppControllers.getController();
            controller.updateServerFileList(files);
        });

        stage.setOnCloseRequest(event -> System.exit(0));

        stage.show();
    }

    public void startFX() {
        launch();
    }

}
