package com.example.client.fx;

import com.example.client.LoggerApp;
import com.example.client.network.Network;
import com.example.client.network.RequestFile;
import com.example.client.network.RequestList;
import com.example.client.network.handlers.AppControllers;
import com.example.common.FileInfo;
import com.example.common.network.FileSender;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    VBox leftPanel, rightPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppControllers.setController(this);
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnUploadAction(ActionEvent actionEvent) {
        PanelController tLeftPanel = (PanelController) leftPanel.getProperties().get("ctrl");

        Path uploadFile = tLeftPanel.getCurrentPath().resolve(tLeftPanel.getSelectedFilename());

        if (Files.isDirectory(uploadFile)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select file is directory", ButtonType.OK);
            alert.showAndWait();
        }

        if (tLeftPanel.getSelectedFilename() != null) {
            sendFile(uploadFile);
        } else {
            new Alert(Alert.AlertType.ERROR, "No one file isn't selected for upload",
                    ButtonType.OK).showAndWait();
        }
    }

    private void sendFile(Path path) {
//        TODO NOT WORKING

        Platform.runLater(() -> {
            try {
                FileSender.sendFile(
                        path,
                        Network.getNetwork().getChannel(),
                        finishListener -> {
                            if (!finishListener.isSuccess()) {
                                JOptionPane.showMessageDialog(null, "File's fail downloaded");
                                LoggerApp.info(finishListener.cause().getMessage());
                            }
                            if (finishListener.isSuccess()) {
                                JOptionPane.showMessageDialog(null, "File's success downloaded");
                                LoggerApp.info("Send file is completed");
                                Network.getNetwork().getChannel().writeAndFlush(new RequestList());
                            }
                        },
                        LoggerApp.getLogger());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    public void btnDownloadAction(ActionEvent actionEvent) {
        PanelController tLeftPanel = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelCloudController tRightPanel = (PanelCloudController) rightPanel.getProperties().get("ctrl");

        if (tRightPanel.getSelectedFilename() != null) {
            LoggerApp.info("Send request on download file: " + tRightPanel.getSelectedFilename());
            Network.getNetwork().getChannel().writeAndFlush(new RequestFile(tRightPanel.getSelectedFilename()));
        } else {
            new Alert(Alert.AlertType.ERROR, "No one file isn't selected for download",
                    ButtonType.OK).showAndWait();
        }
    }

    public void updateServerFileList(List<FileInfo> list) {
        PanelCloudController tRightPanel = (PanelCloudController) rightPanel.getProperties().get("ctrl");
        tRightPanel.updateCloudList(list);
    }

    public void btnUpdateFileList(ActionEvent actionEvent) {
        Network.getNetwork().getChannel().writeAndFlush(new RequestList());
    }
}
