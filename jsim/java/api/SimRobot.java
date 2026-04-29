package api;

import api.Translation2d;
import api.Pose2d;
import api.ChassisSpeeds;
import api.RobotID;
import api.FieldState;
import core.StateManager;

/**
 * Minimal simulation-side robot representation used by example code.
 * The createRobot factory mirrors the usage in examples and stores a simple footprint.
 */
public final class SimRobot {
    private final RobotID robotID;
    private final StateManager stateManager;

    private SimRobot(RobotID robotID, StateManager stateManager) {
        this.robotID = robotID;
        this.stateManager = stateManager;
    }

    public static SimRobot createRobot(Translation2d[] frameDimensions, StateManager stateManager, RobotID robotID) {
        stateManager.initializeRobot(robotID, new Pose2d(0, 0, 0), frameDimensions);
        return new SimRobot(robotID, stateManager);
    }

    public Pose2d getPose() {
        return stateManager.getRobotPose(robotID);
    }

    public RobotID getRobotID() {
        return robotID;
    }

    public void resetPose(Pose2d pose) {
        stateManager.resetRobotPose(robotID, pose);
    }

    public void setChassisSpeeds(ChassisSpeeds speeds) {
        stateManager.setChassisSpeeds(robotID, speeds);
    }

    public FieldState<RobotState> getStateManagerRef() {
        return stateManagerRef;
    }
}
