package com.jtulayan.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;

public class MPGenController {

    @FXML
    private Pane root;

    @FXML
    public void initialize() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.showOpenDialog(root.getScene().getWindow());
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Open");
    }
}
