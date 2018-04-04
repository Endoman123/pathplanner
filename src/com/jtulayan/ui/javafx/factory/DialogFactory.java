package com.jtulayan.ui.javafx.factory;

import com.jtulayan.ui.javafx.ResourceLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogFactory {
    private DialogFactory() { }

    public static Dialog<Boolean> createAboutDialog() {
        Dialog<Boolean> dialog = new Dialog<>();

        try {
            FXMLLoader loader = new FXMLLoader(
                    ResourceLoader.getResource("/com/jtulayan/ui/javafx/dialog/AboutDialog.fxml")
            );

            dialog.setDialogPane(loader.load());

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

    public static Dialog<Boolean> createSettingsDialog() {
        Dialog<Boolean> dialog = new Dialog<>();

        try {
            FXMLLoader loader = new FXMLLoader(
                    ResourceLoader.getResource("/com/jtulayan/ui/javafx/dialog/SettingsDialog.fxml")
            );

            dialog.setDialogPane(loader.load());

            // Some header stuff
            dialog.setTitle("Settings");
            dialog.setHeaderText("Manage settings");

            dialog.setResultConverter((ButtonType buttonType) ->
                    buttonType.getButtonData() == ButtonBar.ButtonData.APPLY
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }
}
