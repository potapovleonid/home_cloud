package com.example.client.fx;

import com.example.client.PanelController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.nio.file.Files;
import java.nio.file.Path;

public class Controller {

    @FXML
    VBox leftPanel, rightPanel;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnUploadAction(ActionEvent actionEvent) {
        PanelController tLeftPanel = (PanelController) leftPanel.getProperties().get("ctrl");

        Path uploadFile = tLeftPanel.getCurrentPath().resolve(tLeftPanel.getSelectedFilename());

        if (Files.isDirectory(uploadFile)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select file is directory", ButtonType.OK);
            alert.showAndWait();
        }

        if (tLeftPanel.getSelectedFilename() != null){
//            TODO upload
        } else {
            new Alert(Alert.AlertType.ERROR, "No one file isn't selected for upload",
                    ButtonType.OK).showAndWait();
        }
    }

    public void btnDownloadAction(ActionEvent actionEvent){
        PanelController tLeftPanel = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController tRightPanel = (PanelController) leftPanel.getProperties().get("ctrl");

        if (tRightPanel.getSelectedFilename() != null){
//            TODO download
        } else {
            new Alert(Alert.AlertType.ERROR, "No one file isn't selected for download",
                    ButtonType.OK).showAndWait();
        }
    }
}
