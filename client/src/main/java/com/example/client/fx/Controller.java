package com.example.client.fx;

import com.example.client.LoggerApp;
import com.example.client.network.Network;
import com.example.client.network.networking.RequestDeleteFile;
import com.example.client.network.networking.RequestFile;
import com.example.client.network.networking.RequestList;
import com.example.client.network.handlers.AppControllers;
import com.example.client.network.networking.SendFile;
import com.example.common.FileInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import javax.swing.*;
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

    public void btnChangePassword(ActionEvent actionEvent) {
        ReplaceSceneManager.replaceSceneContent("changePasswordPanel.fxml", "Change password");
    }

    public void btnUpdateFileList(ActionEvent actionEvent) {
        Network.getNetwork().getChannel().writeAndFlush(new RequestList());
    }

    public void btnUploadAction(ActionEvent actionEvent) {
        PanelController tLeftPanel = (PanelController) leftPanel.getProperties().get("ctrl");
        Path uploadFile;

        try {
            uploadFile = tLeftPanel.getCurrentPath().resolve(tLeftPanel.getSelectedFilename());
        } catch (NullPointerException e){
            JOptionPane.showMessageDialog(null, "No one file isn't selected for upload",
                    "Select file", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (Files.isDirectory(uploadFile)) {
            JOptionPane.showMessageDialog(null, "Select file is directory",
                    "Select file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        sendFile(uploadFile);
    }

    private void sendFile(Path path) {
        Network.getNetwork().getChannel().writeAndFlush(
                new SendFile(
                        path,
                        finishListener -> {
                            if (!finishListener.isSuccess()) {
                                JOptionPane.showMessageDialog(null, "File's fail uploaded");
                                LoggerApp.info(finishListener.cause().getMessage());
                            }
                            if (finishListener.isSuccess()) {
                                JOptionPane.showMessageDialog(null, "File's success uploaded");
                                LoggerApp.info("Send file is completed");
                                Network.getNetwork().getChannel().writeAndFlush(new RequestList());
                            }
                        },
                        LoggerApp.getLogger()));
    }

    public void btnDownloadAction(ActionEvent actionEvent) {
        PanelCloudController tRightPanel = (PanelCloudController) rightPanel.getProperties().get("ctrl");

        if (tRightPanel.getSelectedFilename() != null) {
            LoggerApp.info("Send request on download file: " + tRightPanel.getSelectedFilename());
            Network.getNetwork().getChannel().writeAndFlush(new RequestFile(tRightPanel.getSelectedFilename()));
        } else {
            JOptionPane.showMessageDialog(null, "No one file isn't selected for download",
                    "Select file", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void updateServerFileList(List<FileInfo> list) {
        PanelCloudController tRightPanel = (PanelCloudController) rightPanel.getProperties().get("ctrl");
        tRightPanel.updateCloudList(list);
    }


    public void btnDeleteFile(ActionEvent actionEvent) {
        PanelController tRightPanel = (PanelController) rightPanel.getProperties().get("ctrl");

        if (tRightPanel.getSelectedFilename() != null){
            LoggerApp.info("File for deleting is " + tRightPanel.getSelectedFilename());
            Network.getNetwork().getChannel().writeAndFlush(new RequestDeleteFile(tRightPanel.getSelectedFilename()));
        } else {
            JOptionPane.showMessageDialog(null, "File for deleted isn't selected");
        }
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

}
