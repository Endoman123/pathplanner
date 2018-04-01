package com.jtulayan.ui.cli;

import com.jtulayan.main.ProfileGenerator;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Helper class to interface with the MPG via CLI
 */
public class MPGenCLI {
    private ProfileGenerator backend;

    public MPGenCLI() {
        backend = new ProfileGenerator();
    }

    /**
     * Generates all trajectories in the input directory and saves them to the output directory
     *
     * @param in  the input directory containing all mpg projects
     * @param out the output directory to export all trajectory files
     * @param ext the file extension to export the trajectories as
     */
    public void export(String in, String out, String ext) {
        String[] paths = null;

        File inputDir = new File(in);

        try {
            if (inputDir.exists() && inputDir.isDirectory()) {
                // Get all project files inside the directory
                // We need to re-loop through each file since this method only gets file names.
                File[] files = inputDir.listFiles((File dir, String name) -> isProjectFile(name));

                paths = new String[files.length];

                // Append the full file path to the file
                for (int i = 0; i < files.length; i++)
                    paths[i] = files[i].getAbsolutePath();

                export(paths, out, ext);
            } else {
                throw new IllegalArgumentException("Invalid import directory!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates all trajectories from each specified project and saves them to the output directory
     *
     * @param in  the file paths for all mpg projects to export
     * @param out the output directory to export all trajectory files
     * @param ext the file extension to export the trajectories as
     */
    public void export(String[] in, String out, String ext) {
        File exportDir = new File(out);

        try {
            exportDir.mkdirs();

            System.out.println("Exporting " + in.length + " files!");
            for (String projectDir : in) {
                // Clear backend
                backend.resetValues();
                backend.clearPoints();
                backend.clearWorkingFiles();

                if (isProjectFile(projectDir)) {
                    File curProj = new File(projectDir);
                    String exportName = curProj.getName();
                    exportName = exportName.substring(0, exportName.lastIndexOf('.')).trim();

                    System.out.println("Loading " + exportName + "...");
                    backend.loadProject(curProj);

                    if (backend.hasWorkingProject()) {
                        if (backend.getWaypointsSize() > 1) {
                            System.out.println("Exporting " + curProj + "...");
                            backend.exportTrajectories(new File(exportDir, exportName), "." + ext);
                        } else {
                            System.out.println("Project " + curProj + " has less than 2 waypoints! Skipping....");
                            System.out.println(backend.getWaypointsList());
                        }
                    } else {
                        System.out.println("Failed to load " + curProj + "! Skipping....");
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid export directory!", e);
        }
    }

    /**
     * Checks whether or not the file name represents a project file.
     *
     * @param filename the name of the file to test against
     * @return whether or not the filename has the project extension, specified in {@link ProfileGenerator}
     */
    private boolean isProjectFile(String filename) {
        return ProfileGenerator.PROJECT_EXTENSION.equals(
                filename.substring(filename.lastIndexOf('.') + 1)
        );
    }
}
