package com.jtulayan.ui;

import com.jtulayan.main.ProfileGenerator;
import jaci.pathfinder.Trajectory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import javax.swing.event.ChangeEvent;
import java.io.File;

public class MPGenController {
    private ProfileGenerator backend;

    @FXML
    private Pane root;

    @FXML
    private TextField
        txtTimeStep,
        txtVelocity,
        txtAcceleration,
        txtJerk,
        txtWheelBaseW,
        txtWheelBaseD;

    @FXML
    private MenuItem mnuOpen;

    @FXML
    private ChoiceBox
        choDriveBase,
        choFitMethod;

    @FXML
    public void initialize() {
        backend = new ProfileGenerator();

        choDriveBase.setItems(FXCollections.observableArrayList("Tank", "Swerve"));
        choDriveBase.setValue(choDriveBase.getItems().get(0));
        choDriveBase.setOnAction((Event e) -> {
            String choice = choDriveBase.getSelectionModel().getSelectedItem().toString().toUpperCase();
            ProfileGenerator.DriveBase db = ProfileGenerator.DriveBase.valueOf(choice);

            backend.setDriveBase(db);
        });

        choFitMethod.setItems(FXCollections.observableArrayList("Cubic", "Quintic"));
        choFitMethod.setValue(choFitMethod.getItems().get(0));
        choFitMethod.setOnAction((Event e) -> {
            String choice = "HERMITE_" + choFitMethod.getSelectionModel().getSelectedItem().toString().toUpperCase();
            Trajectory.FitMethod fm = Trajectory.FitMethod.valueOf(choice);

            backend.setFitMethod(fm);
        });
    }

    @FXML
    private void test() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.showOpenDialog(root.getScene().getWindow());
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Open");
    }

    public void updateDriveBase() {

    }
}
