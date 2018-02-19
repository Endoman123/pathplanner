package com.jtulayan.main;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.*;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.SwerveModifier;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The "backend" of the motion profile generator.
 * Mainly to interface with the entire system.
 * Also handles saving, loading, etc.
 */
public class ProfileGenerator {
    public static final String PROJECT_EXTENSION = "bot";
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

    public ProfileGenerator() {
        POINTS = new ArrayList<>();
        resetValues();
    }

    public boolean saveProjectAs(File path) throws Exception {
        boolean finished = true;

        File dir = path.getParentFile();

        if (dir != null && !dir.exists() && dir.isDirectory())
            dir.mkdirs();

        FileWriter out = new FileWriter(path);
        out.write("" + timeStep + System.lineSeparator());
        out.write("" + velocity + System.lineSeparator());
        out.write("" + acceleration + System.lineSeparator());
        out.write("" + jerk + System.lineSeparator());

        out.close();

        return finished;
    }

    public void addPoint(double x, double y, double angle) {
        POINTS.add(new Waypoint(x, y, angle));
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
    }

    /**
     * Clears all the existing waypoints in the list.
     */
    public void clearPoints() {
        POINTS.clear();
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
    // endregion
}
