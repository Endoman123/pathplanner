package com.jtulayan.ui.javafx.factory;

import com.jtulayan.ui.javafx.ResourceLoader;
import com.jtulayan.ui.javafx.dialog.AddWaypointDialogController;
import com.jtulayan.ui.javafx.dialog.SettingsDialogController;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.Properties;

public class DialogFactory {
    private DialogFactory() { }

    public static Dialog<Boolean> createAboutDialog() {
        Dialog<Boolean> dialog = new Dialog<>();

        try {
            DialogPane pane = null;
            FXMLLoader loader = new FXMLLoader(
                    ResourceLoader.getResource("/com/jtulayan/ui/javafx/dialog/AboutDialog.fxml")
            );

            pane = loader.load();
            pane.autosize();
            dialog.setDialogPane(pane);

            dialog.setResultConverter((ButtonType buttonType) ->
                    buttonType.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE
            );
        } catch (Exception e) {
            e.printStackTrace();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        }

        dialog.setTitle("About");

        return dialog;
    }

    public static Dialog<Properties> createSettingsDialog() {
        Dialog<Properties> dialog = new Dialog<>();

        try {
            DialogPane pane = null;
            FXMLLoader loader = new FXMLLoader(
                    ResourceLoader.getResource("/com/jtulayan/ui/javafx/dialog/SettingsDialog.fxml")
            );

            pane = loader.load();
            SettingsDialogController controller = loader.getController();
            pane.autosize();
            dialog.setDialogPane(pane);

            ((Button) dialog.getDialogPane().lookupButton(ButtonType.APPLY)).setDefaultButton(true);
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(false);

            // Some header stuff
            dialog.setTitle("Settings");
            dialog.setHeaderText("Manage settings");

            dialog.setResultConverter((ButtonType buttonType) -> {
                if (buttonType == ButtonType.APPLY) {
                    Properties properties = new Properties();

                    properties.setProperty("ui.overlayDir", controller.getOverlayDir());
                    properties.setProperty("ui.sourceDisplay", "" + controller.getSourceDisplay());
                    properties.setProperty("ui.addWaypointOnClick", "" + controller.getAddWaypointOnClick());
                    properties.setProperty("ui.colorTankTrajectory", controller.getTankTrajColor().toString());
                    properties.setProperty("ui.colorSourceTrajectory", controller.getSourceTrajColor().toString());
                    properties.setProperty("ui.colorWaypointHighlight", controller.getWPHighlightColor().toString());

                    return properties;
                }

                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }

    public static Dialog<Waypoint> createWaypointDialog() {
        Dialog<Waypoint> dialog = new Dialog<>();

        try {
            FXMLLoader loader = new FXMLLoader(
                    ResourceLoader.getResource("/com/jtulayan/ui/javafx/dialog/AddWaypointDialog.fxml")
            );
            ButtonType add = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            DialogPane pane;

            AddWaypointDialogController controller = null;
            TextField txtWX, txtWY, txtWA;

            pane = loader.load();
            pane.autosize();
            dialog.setDialogPane(pane);

            controller = loader.getController();

            txtWX = controller.getTxtWX();
            txtWY = controller.getTxtWY();
            txtWA = controller.getTxtWA();

            txtWX.setText("0.0");
            txtWY.setText("0.0");
            txtWA.setText("0.0");

            // Some header stuff
            dialog.setTitle("Add Waypoint");
            dialog.setHeaderText("Add a new waypoint");

            dialog.getDialogPane().getButtonTypes().add(add);

            dialog.setResultConverter((ButtonType buttonType) -> {
                if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    double
                        x = Double.parseDouble(txtWX.getText().trim()),
                        y = Double.parseDouble(txtWY.getText().trim()),
                        angle = Double.parseDouble(txtWA.getText().trim());

                    return new Waypoint(x, y, Pathfinder.d2r(angle));
                }

                return null;
            });

            pane.lookupButton(add).addEventFilter(ActionEvent.ACTION, ae -> {
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        }

        return dialog;
    }
}