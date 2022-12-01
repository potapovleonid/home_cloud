package com.example.client;

import com.example.client.fx.Controller;
import com.example.client.network.Network;
import com.example.client.network.RequestList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class FXController extends Application {

    private Scene authScene;

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("authPanel.fxml"));
        authScene = new Scene(loader.load());

        this.stage.setTitle("Home cloud Authorizing");
        this.stage.setScene(authScene);

        Network.getNetwork().setCallbackAuthenticated(resultAuth -> {
            if (resultAuth) {
                LoggerApp.info("Auth success, delete auth pipeline");
                replaceSceneContent("main.fxml");
                Network.getNetwork().getChannel().writeAndFlush(new RequestList());
            } else {
                LoggerApp.info("Please try authenticate again");
            }
        });

        Network.getNetwork().setCallbackGettingFileList(files -> {
            Controller controller = new FXMLLoader(ClientApp.class.getResource("main.fxml")).getController();
            controller.updateServerFileList(files);
        });

        stage.show();
    }

    public void startFX(){
        launch();
    }

    public Parent replaceSceneContent(String fxmlName) throws Exception{
        Parent page = FXMLLoader.load(FXController.class.getResource(fxmlName),
                null, new JavaFXBuilderFactory());
        Scene scene = stage.getScene();
        if (scene == null){
            scene = new Scene(page);
            scene.getStylesheets().add(Objects.requireNonNull(
                    FXController.class.getResource("demo.css")).toExternalForm());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(page);
            stage.setTitle("Home cloud");
        }
        stage.sizeToScene();
        stage.show();
        return page;
    }



}
