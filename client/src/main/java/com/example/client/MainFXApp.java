package com.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class  MainFXApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainFXApp.class.getResource("main.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        stage.setTitle("Home cloud");
        stage.setScene(scene);
        stage.show();
    }

    public void startMainPanel(){
        launch();
    }
}
