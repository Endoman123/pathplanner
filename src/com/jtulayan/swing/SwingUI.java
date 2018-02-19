package com.jtulayan.swing;

import com.jtulayan.main.ProfileGenerator;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import javax.swing.*;
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
    private static JTextField txtTimeStep, txtVelocity, txtAcceleration, txtJerk, txtWheelBaseW, txtWheelBaseD;
    private static JMenuItem mnuFileNew, mnuFileSave, mnuFileSaveAs;
    private static JComboBox<Trajectory.FitMethod> cboFitMethod;
    private static ButtonGroup grpDriveBase;

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
        frmMain.getContentPane().add(pnlTrajectory, BorderLayout.LINE_START);
        // endregion

        // region JMenu
        JMenuBar barMain = new JMenuBar();
        frmMain.setJMenuBar(barMain);

        JMenu mnuFile = new JMenu();
        mnuFile.setText("File");
        barMain.add(mnuFile);

        mnuFileNew = new JMenuItem();
        mnuFileNew.setText("New");
        mnuFileNew.addActionListener((ActionEvent e) -> {
            backend.resetValues();
            backend.clearPoints();
            backend.clearWorkingFiles();

            update();
        });

        mnuFile.add(mnuFileNew);

        mnuFileSave = new JMenuItem();
        mnuFileSave.setText("Save");
        mnuFileSave.setEnabled(false);
        mnuFileSave.addActionListener((ActionEvent e) -> {
            if (!backend.saveWorkingProject())
                JOptionPane.showMessageDialog(
                        frmMain,
                        "Saving failed!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
        });

        mnuFile.add(mnuFileSave);

        mnuFileSaveAs = new JMenuItem();
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

                    if (!backend.saveProjectAs(savePath))
                        JOptionPane.showMessageDialog(
                                frmMain,
                                "Invalid location!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    // endregion
                    break;
                case JFileChooser.ERROR_OPTION:
                case JFileChooser.CANCEL_OPTION:
                default:
                    break;
            }

            update();
        });

        mnuFile.add(mnuFileSaveAs);

        mnuFile.addSeparator();
        // endregion

        JLabel lblTimeStep = new JLabel("Time Step");
        lblTimeStep.setLabelFor(txtTimeStep);
        pnlTrajectory.add(lblTimeStep);

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

        JLabel lblVelocity = new JLabel("Max Velocity");
        lblVelocity.setLabelFor(txtVelocity);
        pnlTrajectory.add(lblVelocity);

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

        txtWheelBaseW = new JTextField();
        txtWheelBaseW.setText("1.464");
        txtWheelBaseW.setToolTipText(
                "Distance (in feet) of drive base width, " +
                        "from the outside edge of left wheel to the outside edge of the right wheel."
        );
        txtWheelBaseW.setInputVerifier(new NumberVerifier());
        txtWheelBaseW.addActionListener((ActionEvent e) -> {
            JTextField field = (JTextField) e.getSource();

            if (field.getInputVerifier().verify(field)) {
                double value = Double.parseDouble(field.getText());

                backend.setWheelBaseW(value);
                System.out.println("Update to " + backend.getWheelBaseW());
            }
        });
        txtWheelBaseW.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                double value = Double.parseDouble(field.getText());

                backend.setWheelBaseW(value);
                System.out.println("Update to " + backend.getWheelBaseW());
            }
        });
        pnlTrajectory.add(txtWheelBaseW);

        txtWheelBaseD = new JTextField();
        txtWheelBaseD.setText("0.0");
        txtWheelBaseD.setEnabled(false);
        txtWheelBaseD.setToolTipText(
                "Distance (in feet) of drive base depth, " +
                "from the front end of the front wheel to the back end of the back wheel."
        );
        txtWheelBaseD.setInputVerifier(new NumberVerifier());
        txtWheelBaseD.addActionListener((ActionEvent e) -> {
            JTextField field = (JTextField) e.getSource();

            if (field.getInputVerifier().verify(field)) {
                double value = Double.parseDouble(field.getText());

                backend.setWheelBaseD(value);
                System.out.println("Update to " + backend.getWheelBaseD());
            }
        });
        txtWheelBaseD.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                double value = Double.parseDouble(field.getText());

                backend.setWheelBaseD(value);
                System.out.println("Update to " + backend.getWheelBaseD());
            }
        });
        pnlTrajectory.add(txtWheelBaseD);

        cboFitMethod = new JComboBox<>(Trajectory.FitMethod.values());
        cboFitMethod.addActionListener((ActionEvent e) -> {
            backend.setFitMethod((Trajectory.FitMethod) cboFitMethod.getSelectedItem());
        });
        pnlTrajectory.add(cboFitMethod);

        grpDriveBase = new ButtonGroup();
        JRadioButton radTank = new JRadioButton("Tank", true), radSwerve = new JRadioButton("Swerve", false);
        ActionListener radBaseListener = (ActionEvent e) -> {
            ProfileGenerator.DriveBase newBase = ProfileGenerator.DriveBase.valueOf(e.getActionCommand().toUpperCase());

            backend.setDriveBase(newBase);
        };

        radTank.setActionCommand("Tank");
        radTank.addActionListener(radBaseListener);
        radSwerve.setActionCommand("Swerve");
        radSwerve.addActionListener(radBaseListener);

        grpDriveBase.add(radTank);
        grpDriveBase.add(radSwerve);

        pnlTrajectory.add(radTank);
        pnlTrajectory.add(radSwerve);
    }

    /**
     * Updates values from backend
     */
    private static void update() {
        txtTimeStep.setText("" + backend.getTimeStep());
        txtVelocity.setText("" + backend.getVelocity());
        txtAcceleration.setText("" + backend.getAcceleration());
        txtJerk.setText("" + backend.getJerk());

        mnuFileSave.setEnabled(backend.hasWorkingProject());
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
