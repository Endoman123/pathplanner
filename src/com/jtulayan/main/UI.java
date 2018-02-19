package com.jtulayan.main;

import javax.swing.*;

public class UI {
    private static JFrame frmMain;

    private static JTextField txtTime, txtVelocity, txtAcceleration, txtJerk, txtWheelBaseW, txtFitMethod;

    public static void main(String[] args) {
        init();

        frmMain.setVisible(true);
    }

    private static void init() {
        frmMain = new JFrame("Motion Profile Generator");
        frmMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frmMain.setSize(1075, 677);
        frmMain.setLocationRelativeTo(null);
        frmMain.setResizable(false);
        frmMain.getContentPane().setLayout(null);

        JPanel pnlTrajectory = new JPanel();
        pnlTrajectory.setBounds(0, 22, 450, 617);
        frmMain.getContentPane().add(pnlTrajectory);
        pnlTrajectory.setLayout(null);
    }
}
