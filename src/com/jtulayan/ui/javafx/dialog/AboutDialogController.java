package com.jtulayan.ui.javafx.dialog;

import com.jtulayan.ui.javafx.ResourceLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import java.awt.*;
import java.net.URI;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Controller for the About dialog
 * Mainly just to initialize the links and version number
 */
public class AboutDialogController {
    @FXML
    private Label lblVersion;

    @FXML
    private Hyperlink
            hlEndomanGithub,
            hlJaciGithub,
            hlVannakaGithub,
            hlMITLicense;

    @FXML
    private Button btnViewRepo;

    @FXML
    private void initialize() {
        Manifest manifest = ResourceLoader.getManifest();
        String versionNum = "1.4.0";

        if (manifest != null) {
            Attributes mfAttr = manifest.getMainAttributes();
            String mfVersion = mfAttr.getValue("Version");

            if (mfVersion != null)
                versionNum = mfVersion;
        }

        lblVersion.setText("v" + versionNum);

        hlEndomanGithub.setOnAction((ActionEvent e) -> openLink("https://github.com/Endoman123"));
        hlJaciGithub.setOnAction((ActionEvent e) -> openLink("https://github.com/JacisNonsense"));
        hlVannakaGithub.setOnAction((ActionEvent e) -> openLink("https://github.com/Vannaka"));
        hlMITLicense.setOnAction((ActionEvent e) -> openLink("https://opensource.org/licenses/MIT"));

        btnViewRepo.setOnAction((ActionEvent e) -> openLink("https://github.com/Endoman123/motion-profile-generator"));
    }

    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
