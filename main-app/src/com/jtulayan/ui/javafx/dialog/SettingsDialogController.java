package com.jtulayan.ui.javafx.dialog;

import com.jtulayan.ui.javafx.PropWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

    @FXML
    private CheckBox chkAddWaypointOnClick;

    @FXML
    private ColorPicker
        colTankTraj,
        colSourceTraj,
        colWPHighlight;

    private Properties properties;

    @FXML
    private void initialize() {
        properties = PropWrapper.getProperties();

        txtOverlayDir.setText(properties.getProperty("ui.overlayDir", ""));

        choSourceDisplay.setItems(FXCollections.observableArrayList("None", "Waypoints only", "Waypoints + Source"));
        choSourceDisplay.getSelectionModel().select(Integer.parseInt(properties.getProperty("ui.sourceDisplay", "2")));

        chkAddWaypointOnClick.setSelected(Boolean.parseBoolean(properties.getProperty("ui.addWaypointOnClick", "false")));

        colTankTraj.setValue(Color.web(properties.getProperty(
                "ui.colorTankTrajectory",
                "magenta"
        )));

        colSourceTraj.setValue(Color.valueOf(properties.getProperty(
                "ui.colorSourceTrajectory",
                "orange"
        )));

        colWPHighlight.setValue(Color.valueOf(properties.getProperty(
                "ui.colorWaypointHighlight",
                "green"
        )));
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
