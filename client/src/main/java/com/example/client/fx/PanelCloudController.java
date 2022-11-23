package com.example.client.fx;

import com.example.common.FileInfo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PanelCloudController implements Initializable {

    @FXML
    TableView<FileInfo> rightPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);


        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
        fileNameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFilename()));
        fileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<FileInfo, Long>("Size");
        fileSizeColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getSize()));
        fileSizeColumn.setPrefWidth(100);

        fileSizeColumn.setCellFactory(column -> new TableCell<FileInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "[DIR]";
                    }
                    setText(text);
                }
            }
        });

        rightPanel.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn);
        rightPanel.getSortOrder().add(fileTypeColumn);
    }

    public void updateCloudList(List<FileInfo> list) {
        rightPanel.getItems().clear();
        rightPanel.getItems().addAll(list);
        rightPanel.sort();
    }

    public String getSelectedFilename() {
        if (!rightPanel.isFocused()) {
            return null;
        }
        return rightPanel.getSelectionModel().getSelectedItem().getFilename();
    }

}
