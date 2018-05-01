package com.jtulayan.main;

import com.jtulayan.ui.cli.MPGenCLI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.cli.*;

import java.awt.*;

public class Client extends Application {
    private static CommandLine cmd;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = FXMLLoader.load(getClass().getResource("/com/jtulayan/ui/javafx/MainFXUI.fxml"));
        Dimension res = Toolkit.getDefaultToolkit().getScreenSize();

        root.autosize();
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.setTitle("Pathplanner");

        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        buildOptions(options);

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("pathplanner [OPTIONS]...", options);
                System.exit(0); // Break out here; there's no reason to use other arguments if you need help.
            }

            // No-GUI mode check.
            // If in No-GUI mode, make sure to exit when done.
            if(!cmd.hasOption("n")) {
                launch();
            }

            // TODO: Figure out if this should only be done when in no-gui mode
            if (cmd.hasOption("i")) {
                String inDir = cmd.getOptionValue("i");
                String outDir = cmd.getOptionValue("o", inDir);
                String ext = cmd.getOptionValue("x", "csv");

                MPGenCLI gen = new MPGenCLI();
                gen.export(inDir, outDir, ext);
            } else if (cmd.hasOption("f")) {
                String[] files = cmd.getOptionValues("f");
                String outDir = cmd.getOptionValue("o", System.getProperty("user.dir"));
                String ext = cmd.getOptionValue("x", "csv");

                MPGenCLI gen = new MPGenCLI();
                gen.export(files, outDir, ext);
            }
        } catch (Exception e) {
            // TODO: Figure out why the program doesn't close when an exception is thrown
            System.out.println("Oops, something went wrong!");
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private static void buildOptions(Options opt) {
        // Flags
        Option help = new Option("h", "help", false,"display help dialog");
        Option noGUI = new Option("n", "no-gui", false,"no-gui mode");

        // Args
        Option outputDir = Option.builder("o")
                .longOpt("output-dir")
                .hasArg(true)
                .argName("dir")
                .desc("define export directory for all trajectories")
                .build();

        Option ext = Option.builder("x")
                .longOpt("file-extension")
                .hasArg(true)
                .argName("ext")
                .desc("define the filetype for the exported trajectories; possible options: csv, traj")
                .build();

        Option importDir = Option.builder("i")
                .longOpt("import-dir")
                .hasArg(true)
                .argName("dir")
                .desc("define directory containing all project files to generate trajectories for")
                .build();

        Option importFiles = Option.builder("f")
                .longOpt("import-files")
                .hasArg(true)
                .argName("file1,file2,...")
                .valueSeparator(',')
                .desc("define project files to generate trajectories for")
                .build();

        opt.addOption(help);
        opt.addOption(noGUI);
        opt.addOption(outputDir);
        opt.addOption(importDir);
        opt.addOption(importFiles);
        opt.addOption(ext);
    }
}
