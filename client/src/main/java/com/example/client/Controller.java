package com.example.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TableView<FileInfo> filesTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);


        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFilename()));
        fileTypeColumn.setPrefWidth(240);

        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn);
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

}
