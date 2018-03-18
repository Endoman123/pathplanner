package com.jtulayan.ui.cli;

import com.jtulayan.main.ProfileGenerator;

import java.io.File;

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
        String[] files = null;

        File inputDir = new File(in);

        try {
            if (inputDir.exists() && inputDir.isDirectory()) {
                // Get all project files inside the directory
                files = inputDir.list((File dir, String name) -> isProjectFile(name));

                export(files, out, ext);
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
            if (exportDir.isDirectory()) {
                System.out.println("Exporting " + in.length + " files!");
                for (String projectDir : in) {
                    if (isProjectFile(projectDir)) {
                        File curProj = new File(projectDir);
                        String exportName = curProj.getName();
                        exportName = exportName.substring(0, exportName.lastIndexOf('.'));

                        System.out.println("Loading " + exportName + "...");
                        backend.loadProject(curProj);

                        System.out.println("Exporting " + exportName + "...");
                        backend.exportTrajectories(new File(exportDir, exportName), ext);
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid export directory!");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
