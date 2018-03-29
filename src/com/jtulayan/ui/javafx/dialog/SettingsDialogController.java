package com.jtulayan.ui.javafx.dialog;

import com.jtulayan.ui.javafx.PropWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Properties;

public class SettingsDialogController {
    @FXML
    private Pane root;

    @FXML
    private TextField txtOverlayDir;

    @FXML
    private Button btnChooseOverlay;

    @FXML
    private ChoiceBox<String> choSourceDisplay;

    private Properties properties;

    @FXML
    private void initialize() {
        properties = PropWrapper.getProperties();

        txtOverlayDir.setText(properties.getProperty("ui.overlayDir", ""));

        choSourceDisplay.setItems(FXCollections.observableArrayList("None", "Waypoints only", "Waypoints + Source"));
        choSourceDisplay.getSelectionModel().select(Integer.parseInt(properties.getProperty("ui.sourceDisplay", "2")));
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
                        "*.jpeg",
                        "*.png"
                )
        );

        File result = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (result != null && result.exists() && !result.isDirectory()) {
            txtOverlayDir.setText(result.getAbsolutePath());
        }
    }
}
