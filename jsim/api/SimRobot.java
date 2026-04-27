package jsim.api;

import jsim.driver.WPILibClones.Translation2d;
import jsim.core.StateManager;
import jsim.api.RobotID;
import jsim.driver.WPILibClones.Pose2d;
import jsim.driver.WPILibClones.ChassisSpeeds;

public class SimRobot {
    private final RobotID robotID;
    private final StateManager stateManager;

    public SimRobot(Translation2d[] frameDimensions, StateManager stateManager, RobotID robotID) {
        this.stateManager = stateManager;
        this.robotID = robotID;
        // TODO: Register robot in StateManager
    }

    public Pose2d getPose() {
        // TODO: Pull from StateManager
        return null;
    }

    public RobotID getRobotID() {
        return robotID;
    }

    public void resetPose(Pose2d pose) {
        // TODO: Send to StateManager
    }

    public void setChassisSpeeds(ChassisSpeeds speeds) {
        // TODO: Send to StateManager
    }
}
