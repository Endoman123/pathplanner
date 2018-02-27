package com.jtulayan.ui.javafx;

import com.jtulayan.main.PropWrapper;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class SettingsDialogController {
    @FXML
    private Pane root;

    @FXML
    private TextField txtOverlayDir;

    @FXML
    private Button btnChooseOverlay;

    @FXML
    private ChoiceBox<String> choUnits;

    private Properties properties;

    @FXML
    private void initialize() {
        choUnits.getItems().addAll("Imperial", "Metric");

        properties = PropWrapper.getProperties();

        txtOverlayDir.setText(properties.getProperty("ui.overlayDir", ""));
        choUnits.setValue(properties.getOrDefault("ui.units", "Imperial").toString());
    }

    @FXML
    private void showChooseOverlayDialog() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Find Position Map Overlay");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.jpg",
                        "*.png"
                )
        );

        File result = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (result != null && result.exists() && !result.isDirectory()) {
            txtOverlayDir.setText(result.getAbsolutePath());
        }
    }
}
