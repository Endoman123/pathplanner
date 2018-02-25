package com.jtulayan.ui.javafx;

import com.jtulayan.main.ProfileGenerator;
import com.sun.javafx.collections.ObservableListWrapper;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

import java.awt.*;
import java.io.File;
import java.util.*;

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
    private Label
        lblWheelBaseD;

    @FXML
    private TableView<Waypoint> tblWaypoints;

    @FXML
    private LineChart<Double, Double>
            chtPosition,
            chtVelocity;

    @FXML
    private TableColumn<Waypoint, Double>
            colWaypointX,
            colWaypointY,
            colWaypointAngle;

    @FXML
    private MenuItem
            mnuOpen,
            mnuFileNew,
            mnuFileSave,
            mnuFileSaveAs,
            mnuFileExportAsCSV,
            mnuFileExit;

    @FXML
    private ChoiceBox
            choDriveBase,
            choFitMethod;

    @FXML
    private Button
            btnAddPoint,
            btnClearPoints,
            btnDeleteLast;

    private ObservableList<Waypoint> waypointsList;
    private ObservableList<XYChart.Series<Double, Double>> trajPosList;

    @FXML
    public void initialize() {
        backend = new ProfileGenerator();

        choDriveBase.setItems(FXCollections.observableArrayList("Tank", "Swerve"));
        choDriveBase.setValue(choDriveBase.getItems().get(0));
        choDriveBase.setOnAction(this::updateDriveBase);

        choFitMethod.setItems(FXCollections.observableArrayList("Cubic", "Quintic"));
        choFitMethod.setValue(choFitMethod.getItems().get(0));
        choFitMethod.setOnAction(this::updateFitMethod);

        Callback<TableColumn<Waypoint, Double>, TableCell<Waypoint, Double>> doubleCallback =
            (TableColumn<Waypoint, Double> param) -> {
                TextFieldTableCell<Waypoint, Double> cell = new TextFieldTableCell<>();

                cell.setConverter(new DoubleStringConverter());

                return cell;
        };

        EventHandler<TableColumn.CellEditEvent<Waypoint, Double>> editHandler =
            (TableColumn.CellEditEvent<Waypoint, Double> t) -> {
                int ind = t.getTablePosition().getRow();
                Waypoint newWaypoint = t.getRowValue();

                if (t.getTableColumn() == colWaypointAngle)
                    backend.editWaypoint(ind, newWaypoint.x, newWaypoint.y, t.getNewValue());
                else if (t.getTableColumn() == colWaypointY)
                    backend.editWaypoint(ind, newWaypoint.x, t.getNewValue(), newWaypoint.angle);
                else
                    backend.editWaypoint(ind, t.getNewValue(), newWaypoint.y, newWaypoint.angle);
        };

        colWaypointX.setCellFactory(doubleCallback);
        colWaypointY.setCellFactory(doubleCallback);
        colWaypointAngle.setCellFactory(doubleCallback);

        colWaypointX.setOnEditCommit(editHandler);
        colWaypointY.setOnEditCommit(editHandler);
        colWaypointAngle.setOnEditCommit(editHandler);

        colWaypointX.setCellValueFactory((TableColumn.CellDataFeatures<Waypoint, Double> d) ->
            new ObservableValueBase<Double>() {
                @Override
                public Double getValue() {
                    return d.getValue().x;
                }
            }
        );

        colWaypointY.setCellValueFactory((TableColumn.CellDataFeatures<Waypoint, Double> d) ->
            new ObservableValueBase<Double>() {
                @Override
                public Double getValue() {
                    return d.getValue().y;
                }
           }
        );

        colWaypointAngle.setCellValueFactory((TableColumn.CellDataFeatures<Waypoint, Double> d) ->
            new ObservableValueBase<Double>() {
                @Override
                public Double getValue() {
                    return Pathfinder.r2d(d.getValue().angle);
                }
            }
        );

        waypointsList = new ObservableListWrapper<>(backend.getWaypoints());

        tblWaypoints.setItems(waypointsList);
    }

    @FXML
    private void showSaveAsDialog() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Extensive Markup Language", "*.xml")
        );

        File result = fileChooser.showSaveDialog(root.getScene().getWindow());

        if (result != null && !backend.saveProjectAs(result)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Save Failed");
            alert.setHeaderText("Failed to Save Project!");
            alert.setContentText("An error has occured with saving! Please try again.");

            alert.showAndWait();
        } else if (backend.hasWorkingProject()) {
            mnuFileSave.setDisable(false);
        }
    }

    @FXML
    private void save() {
        if (!backend.saveWorkingProject()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Save Failed");
            alert.setHeaderText("Failed to Save Project!");
            alert.setContentText("An error has occured with saving! Please try again.");

            alert.showAndWait();
        }
    }

    @FXML
    private void showExportAsCSVDialog() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Export as CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Comma Separated Values", "*.csv")
        );

        File result = fileChooser.showSaveDialog(root.getScene().getWindow());

        if (result != null && generateTrajectories()) {
            String parentPath = result.getAbsolutePath();
            parentPath = parentPath.substring(0, parentPath.lastIndexOf(".csv"));

            backend.exportTrajectoriesCSV(new File(parentPath));
        }
    }

    @FXML
    private void showAddPointDialog() {
        Dialog<Waypoint> waypointDialog = new Dialog<>();
        ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        Optional<Waypoint> result = null;
        GridPane grid = new GridPane();
        TextField
                txtWX = new TextField(),
                txtWY = new TextField(),
                txtWA = new TextField();

        // Some header stuff
        waypointDialog.setTitle("Add Point");
        waypointDialog.setHeaderText("Add a new waypoint");

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("X:"), 0, 0);
        grid.add(txtWX, 1, 0);
        grid.add(new Label("Y:"), 0, 1);
        grid.add(txtWY, 1, 1);
        grid.add(new Label("Angle:"), 0, 2);
        grid.add(txtWA, 1, 2);

        waypointDialog.getDialogPane().setContent(grid);

        // Add all buttons
        waypointDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        waypointDialog.getDialogPane().lookupButton(loginButtonType).addEventFilter(ActionEvent.ACTION, ae -> {
            try {
                Double.parseDouble(txtWX.getText().trim());
                Double.parseDouble(txtWY.getText().trim());
                Double.parseDouble(txtWA.getText().trim());
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("Invalid Point!");
                alert.setHeaderText("Invalid point input!");
                alert.setContentText("Please check your fields and try again.");

                Toolkit.getDefaultToolkit().beep();
                alert.showAndWait();
                ae.consume();
                e.printStackTrace();
            }
        });


        waypointDialog.setResultConverter((ButtonType buttonType) -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                double
                        x = Double.parseDouble(txtWX.getText().trim()),
                        y = Double.parseDouble(txtWY.getText().trim()),
                        angle = Double.parseDouble(txtWA.getText().trim());

                return new Waypoint(x, y, Pathfinder.d2r(angle));
            }

            return null;
        });

        // Wait for the result
        result = waypointDialog.showAndWait();

        result.ifPresent((Waypoint w) -> {
            backend.addPoint(w.x, w.y, w.angle);

            tblWaypoints.refresh();
        });
    }

    @FXML
    private void showClearPointsDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Clear Points");
        alert.setHeaderText("Clear All Points?");
        alert.setContentText("Are you sure you want to clear all points?");

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent((ButtonType t) -> {
            if (t.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                backend.clearPoints();

                tblWaypoints.refresh();
            }
        });
    }

    @FXML
    private void resetData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Create New Project?");
        alert.setHeaderText("Confirm Reset");
        alert.setContentText("Are you sure you want to reset all data? Have you saved?");

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent((ButtonType t) -> {
            if (t == ButtonType.OK) {
                backend.resetValues();
                backend.clearWorkingFiles();

                txtTimeStep.setText("" + backend.getTimeStep());
                txtVelocity.setText("" + backend.getVelocity());
                txtAcceleration.setText("" + backend.getAcceleration());
                txtJerk.setText("" + backend.getJerk());
                txtWheelBaseW.setText("" + backend.getWheelBaseW());
                txtWheelBaseD.setText("" + backend.getWheelBaseD());

                choDriveBase.setValue("Tank");
                choFitMethod.setValue("Cubic");

                mnuFileSave.setDisable(true);
            }
        });
    }

    @FXML
    private void synchronizeTextData() {
        backend.setTimeStep(Double.parseDouble(txtTimeStep.getText().trim()));
        backend.setVelocity(Double.parseDouble(txtVelocity.getText().trim()));
        backend.setAcceleration(Double.parseDouble(txtAcceleration.getText().trim()));
        backend.setJerk(Double.parseDouble(txtJerk.getText().trim()));
        backend.setWheelBaseW(Double.parseDouble(txtWheelBaseW.getText().trim()));
        backend.setWheelBaseD(Double.parseDouble(txtWheelBaseD.getText().trim()));
    }

    @FXML
    private void exit() {
        System.exit(0);
    }

    @FXML
    private boolean generateTrajectories() {
        if (backend.getWaypointsSize() < 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Error!");
            alert.setHeaderText("Cannot Generate Trajectories!");
            alert.setContentText("Make sure you have at least two waypoints before trying to generate a trajectory!");

            alert.showAndWait();

            return false;
        } else {
            synchronizeTextData();

            backend.updateTrajectories();

            repopulatePosChart();
            repopulateVelChart();

            return true;
        }
    }

    private void updateDriveBase(Event e) {
        String choice = ((ChoiceBox<String>)e.getSource()).getSelectionModel().getSelectedItem().toUpperCase();
        ProfileGenerator.DriveBase db = ProfileGenerator.DriveBase.valueOf(choice);

        backend.setDriveBase(db);

        txtWheelBaseD.setDisable(db == ProfileGenerator.DriveBase.TANK);
        lblWheelBaseD.setDisable(db == ProfileGenerator.DriveBase.TANK);
    }

    private void updateFitMethod(Event e) {
        String choice = ((ChoiceBox<String>)e.getSource()).getSelectionModel().getSelectedItem().toUpperCase();
        Trajectory.FitMethod fm = Trajectory.FitMethod.valueOf("HERMITE_" + choice);

        backend.setFitMethod(fm);
    }

    private void repopulatePosChart() {
        SegmentSeries
            fl = new SegmentSeries(backend.getFrontLeftTrajectory()),
            fr = new SegmentSeries(backend.getFrontRightTrajectory());

        XYChart.Series<Double, Double>
            flSeries = fl.getPositionSeries(),
            frSeries = fr.getPositionSeries();

        // Clear data from position graph
        chtPosition.getData().clear();

        if (backend.getDriveBase() == ProfileGenerator.DriveBase.SWERVE) {
            SegmentSeries
                    bl = new SegmentSeries(backend.getBackLeftTrajectory()),
                    br = new SegmentSeries(backend.getBackRightTrajectory());

            XYChart.Series<Double, Double>
                    blSeries = bl.getPositionSeries(),
                    brSeries = br.getPositionSeries();

            chtPosition.getData().addAll(blSeries, brSeries, flSeries, frSeries);
            flSeries.getNode().setStyle("-fx-stroke: red");
            frSeries.getNode().setStyle("-fx-stroke: red");
            blSeries.getNode().setStyle("-fx-stroke: blue");
            brSeries.getNode().setStyle("-fx-stroke: blue");
        } else {
            chtPosition.getData().addAll(flSeries, frSeries);

            flSeries.getNode().setStyle("-fx-stroke: magenta");
            frSeries.getNode().setStyle("-fx-stroke: magenta");
        }
    }

    private void repopulateVelChart() {
        SegmentSeries
                fl = new SegmentSeries(backend.getFrontLeftTrajectory()),
                fr = new SegmentSeries(backend.getFrontRightTrajectory());

        XYChart.Series<Double, Double>
                flSeries = fl.getVelocitySeries(),
                frSeries = fr.getVelocitySeries();

        // Clear data from velocity graph
        chtVelocity.getData().clear();
        chtVelocity.getData().addAll(flSeries, frSeries);

        if (backend.getDriveBase() == ProfileGenerator.DriveBase.SWERVE) {
            SegmentSeries
                    bl = new SegmentSeries(backend.getBackLeftTrajectory()),
                    br = new SegmentSeries(backend.getBackRightTrajectory());

            XYChart.Series<Double, Double>
                    blSeries = bl.getVelocitySeries(),
                    brSeries = br.getVelocitySeries();

            chtVelocity.getData().addAll(blSeries, brSeries);

            flSeries.setName("Front Left Trajectory");
            frSeries.setName("Front Right Trajectory");
            blSeries.setName("Back Left Trajectory");
            brSeries.setName("Back Right Trajectory");
        } else {
            flSeries.setName("Left Trajectory");
            frSeries.setName("Right Trajectory");
        }
    }
}
