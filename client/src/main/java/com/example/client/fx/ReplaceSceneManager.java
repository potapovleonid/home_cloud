package com.example.client.fx;

import com.example.client.FXController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

public class ReplaceSceneManager {
    private static Stage stage;

    public static void setStage(Stage stage) {
        ReplaceSceneManager.stage = stage;
    }

    public static void replaceSceneContent(String fxmlName){
        if (stage == null){
            JOptionPane.showMessageDialog(null, "Stage were'nt set in ReplaceSceneManager");
            System.exit(0);
            return;
        }

        Platform.runLater(() -> {
            try {
                Parent page = FXMLLoader.load(Objects.requireNonNull(FXController.class.getResource(fxmlName)),
                        null, new JavaFXBuilderFactory());
                Scene scene = stage.getScene();
                if (scene == null) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
