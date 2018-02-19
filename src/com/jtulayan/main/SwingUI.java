package com.jtulayan.main;

import pathGenerator.Gui2;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Swing frontend for generator
 */
public class SwingUI {
    private static ProfileGenerator backend;

    private static JFrame frmMain;
    private static JTextField txtTimeStep, txtVelocity, txtAcceleration, txtJerk, txtWheelBaseW, txtFitMethod;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                backend = new ProfileGenerator();

                init();
                frmMain.setVisible(true);
                frmMain.getContentPane().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void init() {
        // region JFrame
        frmMain = new JFrame("Motion Profile Generator");
        frmMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frmMain.setSize(1075, 677);
        frmMain.setLocationRelativeTo(null);
        frmMain.setResizable(false);
        frmMain.setLayout(new FlowLayout());
        // endregion

        // region JPanel
        JPanel pnlTrajectory = new JPanel();
        pnlTrajectory.setBounds(0, 22, 450, 617);
        frmMain.getContentPane().add(pnlTrajectory);
        // endregion

        // region JMenu
        JMenuBar barMain = new JMenuBar();
        frmMain.setJMenuBar(barMain);

        JMenu mnuFile = new JMenu();
        mnuFile.setText("File");
        barMain.add(mnuFile);


        JMenuItem mnuFileNew = new JMenuItem();
        mnuFileNew.setText("New");
        mnuFileNew.addActionListener((ActionEvent e) -> {
            backend.resetValues();
            backend.clearPoints();

            update();
        });

        mnuFile.add(mnuFileNew);

        JMenuItem mnuFileSaveAs = new JMenuItem();
        mnuFileSaveAs.setText("Save As...");
        mnuFileSaveAs.addActionListener((ActionEvent e) -> {
            JFileChooser saveChooser = new JFileChooser();

            saveChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            saveChooser.setDialogTitle("Save As");

            saveChooser.setFileFilter(new FileNameExtensionFilter(
                    "Motion Profile Project Files (." + ProfileGenerator.PROJECT_EXTENSION + ")",
                    ProfileGenerator.PROJECT_EXTENSION
            ));

            switch (saveChooser.showSaveDialog(frmMain)) {
                case JFileChooser.APPROVE_OPTION:
                    File savePath = saveChooser.getSelectedFile();

                    if (!savePath.getAbsolutePath().endsWith("." + ProfileGenerator.PROJECT_EXTENSION))
                        savePath = new File(saveChooser.getSelectedFile() + "." + ProfileGenerator.PROJECT_EXTENSION);

                    if (savePath.exists()) {
                        boolean overwrite = JOptionPane.showConfirmDialog(
                                frmMain,
                                "File exists! Overwrite?",
                                "Save As",
                                JOptionPane.YES_NO_OPTION
                        ) == JOptionPane.YES_OPTION;

                        if (!overwrite)
                            break;
                    }

                    // region Try-Catch
                    try {
                        if (!backend.saveProjectAs(savePath))
                            JOptionPane.showMessageDialog(
                                    frmMain,
                                    "Invalid location!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                frmMain,
                                "Invalid location!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                    // endregion
                    break;
                case JFileChooser.ERROR_OPTION:
                case JFileChooser.CANCEL_OPTION:
                default:
                    break;
            }
        });

        mnuFile.add(mnuFileSaveAs);

        mnuFile.addSeparator();
        // endregion

        txtTimeStep = new JTextField();
        txtTimeStep.setText("0.05");
        txtTimeStep.setToolTipText(
            "The time to servo trajectory points in milliseconds. " +
            "The minimum value is 1ms and the maximum is 255ms. If ‘0’ is sent, it will be interpreted as 1ms."
        );

        txtTimeStep.setInputVerifier(new NumberVerifier());

        txtTimeStep.addActionListener((ActionEvent e) -> {
            JTextField field = (JTextField) e.getSource();

            if (field.getInputVerifier().verify(field)) {
                double value = Double.parseDouble(field.getText());

                backend.setTimeStep(value);
                System.out.println("Update to " + backend.getTimeStep());
            }
        });

        txtTimeStep.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                double value = Double.parseDouble(field.getText());

                backend.setTimeStep(value);
                System.out.println("Update to " + backend.getTimeStep());
            }
        });

        pnlTrajectory.add(txtTimeStep);

        txtVelocity = new JTextField();
        txtVelocity.setText("4");
        txtVelocity.setToolTipText(
                "This is the velocity to feed-forward when this trajectory point is loaded into the MPE."
        );

        txtVelocity.setInputVerifier(new NumberVerifier());

        txtVelocity.addActionListener((ActionEvent e) -> {
            JTextField field = (JTextField) e.getSource();

            if (field.getInputVerifier().verify(field)) {
                double value = Double.parseDouble(field.getText());

                backend.setVelocity(value);
                System.out.println("Update to " + backend.getVelocity());
            }
        });

        txtVelocity.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                double value = Double.parseDouble(field.getText());

                backend.setVelocity(value);
                System.out.println("Update to " + backend.getVelocity());
            }
        });

        pnlTrajectory.add(txtVelocity);

        txtAcceleration = new JTextField();
        txtAcceleration.setText("3");
        txtAcceleration.setToolTipText(
                "Max acceleration to maintain when running through trajectory"
        );

        txtAcceleration.setInputVerifier(new NumberVerifier());

        txtAcceleration.addActionListener((ActionEvent e) -> {
            JTextField field = (JTextField) e.getSource();

            if (field.getInputVerifier().verify(field)) {
                double value = Double.parseDouble(field.getText());

                backend.setAcceleration(value);
                System.out.println("Update to " + backend.getAcceleration());
            }
        });

        txtAcceleration.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                double value = Double.parseDouble(field.getText());

                backend.setAcceleration(value);
                System.out.println("Update to " + backend.getAcceleration());
            }
        });

        pnlTrajectory.add(txtAcceleration);

        txtJerk = new JTextField();
        txtJerk.setText("60");
        txtJerk.setToolTipText(
                "Derivative of acceleration; describes the max rate at which to change acceleration"
        );

        txtJerk.setInputVerifier(new NumberVerifier());

        txtJerk.addActionListener((ActionEvent e) -> {
            JTextField field = (JTextField) e.getSource();

            if (field.getInputVerifier().verify(field)) {
                double value = Double.parseDouble(field.getText());

                backend.setJerk(value);
                System.out.println("Update to " + backend.getJerk());
            }
        });

        txtJerk.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                double value = Double.parseDouble(field.getText());

                backend.setJerk(value);
                System.out.println("Update to " + backend.getJerk());
            }
        });

        pnlTrajectory.add(txtJerk);
    }

    /**
     * Updates values from backend
     */
    private static void update() {
        txtTimeStep.setText("" + backend.getTimeStep());
        txtVelocity.setText("" + backend.getVelocity());
        txtAcceleration.setText("" + backend.getAcceleration());
        txtJerk.setText("" + backend.getJerk());
    }

    /**
     * Custom input verifier that test for numerical input.
     */
    private static class NumberVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean canParse = true;
            try {
                JTextField field = (JTextField) input;
                Double.parseDouble(field.getText());
            } catch (Exception e) {
                canParse = false;
                Toolkit.getDefaultToolkit().beep();
            }

            return canParse;
        }
    }
}
