package com.example.client.fx;

import com.example.client.LoggerApp;
import com.example.client.network.Network;
import com.example.common.FileInfo;
import com.example.common.network.FileSender;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
            sendFile(tLeftPanel);
        } else {
            new Alert(Alert.AlertType.ERROR, "No one file isn't selected for upload",
                    ButtonType.OK).showAndWait();
        }
    }

    private void sendFile(PanelController tLeftPanel) {
        try {
            FileSender.sendFile(
                    tLeftPanel.getCurrentPath(),
                    Network.getNetwork().getChannel(),
                    finishListener -> {
                        if (!finishListener.isSuccess()){
                            JOptionPane.showMessageDialog(null, "File's fail downloaded");
                            LoggerApp.info(finishListener.cause().getMessage());
                        }
                        if (finishListener.isSuccess()){
                            JOptionPane.showMessageDialog(null, "File's success downloaded");
                            LoggerApp.info("Send file is completed");
                        }
                    },
                    LoggerApp.getLogger());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnDownloadAction(ActionEvent actionEvent){
        PanelController tLeftPanel = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController tRightPanel = (PanelController) rightPanel.getProperties().get("ctrl");

        if (tRightPanel.getSelectedFilename() != null){
//            TODO download
        } else {
            new Alert(Alert.AlertType.ERROR, "No one file isn't selected for download",
                    ButtonType.OK).showAndWait();
        }
    }

    public void updateServerFileList(List<FileInfo> list){
        PanelCloudController tRightPanel = (PanelCloudController) rightPanel.getProperties().get("ctrl");
        tRightPanel.updateCloudList(list);
    }
}
