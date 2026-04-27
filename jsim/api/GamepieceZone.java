package jsim.api;

import jsim.driver.WPILibClones.Rotation3d;

public class GamepieceZone {
    public enum Mode {
        INTAKE,
        OUTTAKE,
        SHOOT,
        DISABLED
    }

    private double exitVelocity;
    private Rotation3d exitRotation;

    public GamepieceZone(SimRobot robot) {
        // TODO: Link to robot and initialize
    }

    public void setMode(Mode mode) {
        // TODO: Implement mode logic
    }

    public void setExitParameters(double velocity, Rotation3d rotation) {
        this.exitVelocity = velocity;
        this.exitRotation = rotation;
    }

    public double getExitVelocity() {
        return exitVelocity;
    }

    public Rotation3d getExitRotation() {
        return exitRotation;
    }
}
