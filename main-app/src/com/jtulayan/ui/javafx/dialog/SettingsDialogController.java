package com.jtulayan.ui.javafx.dialog;

import com.jtulayan.ui.javafx.PropWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;
import java.util.Properties;

public class SettingsDialogController {
    @FXML
    private Pane root;

    @FXML
    private TextField
        txtOverlayDir,
        txtTeamNumber,
        txtIP,
        txtPort;

    @FXML
    private ChoiceBox<String>
        choSourceDisplay,
        choTrajFormat;

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

        choSourceDisplay.setItems(FXCollections.observableArrayList(
                "None",
                "Waypoints only",
                "Waypoints + Source",
                "Robot Bounds + Source"));
        choSourceDisplay.getSelectionModel().select(
                Integer.parseInt(properties.getProperty("ui.sourceDisplay", "2")
        ));

        chkAddWaypointOnClick.setSelected(
                Boolean.parseBoolean(properties.getProperty("ui.addWaypointOnClick", "false")
        ));

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


        choTrajFormat.setItems(FXCollections.observableArrayList(
                "Comma-Separated Values (*.csv)",
                "Binary Trajectory File (*.traj)"
        ));

        choTrajFormat.getSelectionModel().select(
                Integer.parseInt(properties.getProperty("deploy.trajFormat", "0")
        ));

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

    public String getOverlayDir() {
        return txtOverlayDir.getText();
    }

    public int getSourceDisplay() {
        return choSourceDisplay.getSelectionModel().getSelectedIndex();
    }

    public Color getTankTrajColor() {
        return colTankTraj.getValue();
    }

    public Color getSourceTrajColor() {
        return colSourceTraj.getValue();
    }

    public Color getWPHighlightColor() {
        return colWPHighlight.getValue();
    }

    public boolean getAddWaypointOnClick() {
        return chkAddWaypointOnClick.isSelected();
    }

    @FXML
    private void confirmReset() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = null;
        confirm.setHeaderText("Confirm reset");
        confirm.setContentText("Reset settings to default values?");

        result = confirm.showAndWait();
        result.ifPresent((ButtonType b) -> {
            if (b == ButtonType.OK) {
                txtOverlayDir.clear();
                choSourceDisplay.getSelectionModel().select(2);
                chkAddWaypointOnClick.setSelected(true);
                colTankTraj.setValue(Color.MAGENTA);
                colSourceTraj.setValue(Color.ORANGE);
                colWPHighlight.setValue(Color.GREEN);
            }
        });
    }
}

