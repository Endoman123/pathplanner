package com.jtulayan.main;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.SwerveModifier;
import jaci.pathfinder.modifiers.TankModifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The "backend" of the motion profile generator.
 * Mainly to interface with the entire system.
 * Also handles saving, loading, etc.
 */
public class ProfileGenerator {
    public static final String PROJECT_EXTENSION = "xml";
    public enum DriveBase {
        TANK,
        SWERVE
    }

    private double timeStep;
    private double velocity;
    private double acceleration;
    private double jerk;
    private double wheelBaseW;
    private double wheelBaseD;
    private DriveBase driveBase;
    private FitMethod fitMethod;

    private final List<Waypoint> POINTS;

    // Trajectories for both bases
    // Use front-left and front-right for tank drive L and R
    private Trajectory fl;
    private Trajectory fr;
    private Trajectory bl;
    private Trajectory br;

    // Source trajectory
    // i.e. the center trajectory
    private Trajectory source;

    // File stuff
    private File workingProject;

    static {
        try {
            NativeUtils.loadLibraryFromJar("/pathfinderjava.dll");
        } catch (IOException e) {
            e.printStackTrace(); // This is probably not the best way to handle exception :-)
        }
    }

    public ProfileGenerator() {
        POINTS = new ArrayList<>();
        resetValues();
    }

    public boolean saveProjectAs(File path) {
        boolean finished = true;

        if (!path.getAbsolutePath().endsWith("." + PROJECT_EXTENSION))
            path = new File(path + "." + PROJECT_EXTENSION);

        File dir = path.getParentFile();

        if (dir != null && !dir.exists() && dir.isDirectory()) {
            if (!dir.mkdirs())
                return false;
        }

        if (path.exists() && !path.delete())
            return false;

        workingProject = path;

        return saveWorkingProject();
    }

    public boolean saveWorkingProject() {
        boolean finished = true;
        if (workingProject != null) {
            if (workingProject.exists() && !workingProject.delete())
                return false;

            try {
                // Create document
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.newDocument();

                Element trajectoryEle = dom.createElement("Trajectory");

                trajectoryEle.setAttribute("dt", "" + timeStep);
                trajectoryEle.setAttribute("velocity", "" + velocity);
                trajectoryEle.setAttribute("acceleration", "" + acceleration);
                trajectoryEle.setAttribute("jerk", "" + jerk);
                trajectoryEle.setAttribute("wheelBaseW", "" + wheelBaseW);
                trajectoryEle.setAttribute("wheelBaseD", "" + wheelBaseD);
                trajectoryEle.setAttribute("fitMethod", "" + fitMethod.toString());
                trajectoryEle.setAttribute("driveBase", "" + driveBase.toString());

                dom.appendChild(trajectoryEle);

                for (Waypoint w : POINTS) {
                    Element waypointEle = dom.createElement("Waypoint");
                    Element xEle = dom.createElement("X");
                    Element yEle = dom.createElement("Y");
                    Element angleEle = dom.createElement("Angle");
                    Text xText = dom.createTextNode("" + w.x);
                    Text yText = dom.createTextNode("" + w.y);
                    Text angleText = dom.createTextNode("" + w.angle);

                    xEle.appendChild(xText);
                    yEle.appendChild(yText);
                    angleEle.appendChild(angleText);

                    waypointEle.appendChild(xEle);
                    waypointEle.appendChild(yEle);
                    waypointEle.appendChild(angleEle);

                    trajectoryEle.appendChild(waypointEle);
                }

                OutputFormat format = new OutputFormat(dom);

                format.setIndenting(true);

                XMLSerializer xmlSerializer = new XMLSerializer(
                    new FileOutputStream(workingProject), format
                );

                xmlSerializer.serialize(dom);
            } catch (Exception e) {
                finished = false;
                e.printStackTrace();
            }
        } else {
            finished = false;
        }

        return finished;
    }

    public boolean exportTrajectoriesCSV(File parentPath) {
        boolean finished = true;

        try {
            updateTrajectories();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        File dir = parentPath.getParentFile();

        if (dir != null && !dir.exists() && dir.isDirectory()) {
            if (!dir.mkdirs())
                return false;
        }

        try {
            Pathfinder.writeToCSV(new File(parentPath + "_source_detailed.csv"), source);

            if (driveBase == DriveBase.SWERVE) {

            } else {
                Pathfinder.writeToCSV(new File(parentPath + "_left_detailed.csv"), fl);
                Pathfinder.writeToCSV(new File(parentPath + "_right_detailed.csv"), fr);
            }
        } catch (Exception e) {
            finished = false;
        }

        return finished;
    }

    // TODO: Unfinished
    public boolean loadProject(File path) {
        boolean finished = true;

        return finished;
    }

    public void addPoint(double x, double y, double angle) {
        POINTS.add(new Waypoint(x, y, angle));
    }

    public void editWaypoint(int index, double x, double y, double angle) {
        POINTS.get(index).x = x;
        POINTS.get(index).y = y;
        POINTS.get(index).angle = angle;
    }

    public void removePoint(int index) {
        POINTS.remove(index);
    }

    public int getWaypointsSize() {
        return POINTS.size();
    }

    /**
     * Resets configuration to default values
     */
    public void resetValues() {
        timeStep = 0.05;
        velocity = 4;
        acceleration = 3;
        jerk = 60;
        wheelBaseW = 1.464;
        wheelBaseD = 0;

        fitMethod = FitMethod.HERMITE_CUBIC;
        driveBase = DriveBase.TANK;
    }

    /**
     * Clears all the existing waypoints in the list.
     */
    public void clearPoints() {
        POINTS.clear();
    }

    /**
     * Clears the working project files
     */
    public void clearWorkingFiles() {
        workingProject = null;
    }

    /**
     * Updates the trajectories
     */
    public void updateTrajectories() {
        Config config = new Config(fitMethod, Config.SAMPLES_HIGH, timeStep, velocity, acceleration, jerk);
        source = Pathfinder.generate(POINTS.toArray(new Waypoint[1]), config);

        if (driveBase == DriveBase.SWERVE) {
            SwerveModifier swerve = new SwerveModifier(source);

            // There is literally no other swerve mode other than the default can someone please explain this to me
            swerve.modify(wheelBaseW, wheelBaseD, SwerveModifier.Mode.SWERVE_DEFAULT);

            fl = swerve.getFrontLeftTrajectory();
            fr = swerve.getFrontRightTrajectory();
            bl = swerve.getBackLeftTrajectory();
            br = swerve.getBackRightTrajectory();
        } else { // By default, treat everything as tank drive.
            TankModifier tank = new TankModifier(source);
            tank.modify(wheelBaseW);

            fl = tank.getLeftTrajectory();
            fr = tank.getRightTrajectory();
            bl = null;
            br = null;
        }
    }

    // region Getters and Setters
    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public DriveBase getDriveBase() {
        return driveBase;
    }

    public void setDriveBase(DriveBase driveBase) {
        this.driveBase = driveBase;
    }

    public FitMethod getFitMethod() {
        return fitMethod;
    }

    public void setFitMethod(FitMethod fitMethod) {
        this.fitMethod = fitMethod;
    }

    public double getJerk() {
        return jerk;
    }

    public void setJerk(double jerk) {
        this.jerk = jerk;
    }

    public double getWheelBaseW() {
        return wheelBaseW;
    }

    public void setWheelBaseW(double wheelBaseW) {
        this.wheelBaseW = wheelBaseW;
    }

    public double getWheelBaseD() {
        return wheelBaseD;
    }

    public void setWheelBaseD(double wheelBaseD) {
        this.wheelBaseD = wheelBaseD;
    }

    public boolean hasWorkingProject() {
        return workingProject != null;
    }

    public List<Waypoint> getWaypoints() {
        return POINTS;
    }

    public Trajectory getSourceTrajectory() {
        return source;
    }

    public Trajectory getFrontLeftTrajectory() {
        return fl;
    }

    public Trajectory getFrontRightTrajectory() {
        return fr;
    }

    public Trajectory getBackLeftTrajectory() {
        return bl;
    }

    public Trajectory getBackRightTrajectory() {
        return br;
    }

    // endregion
}
