package api;

public class GamepieceZone {
    public enum Mode {
        INTAKE, // Intake gamepiece, motor count irrelevant
        OUTTAKE, // Outtake gamepiece with only one roller (intake, flywheel wihout backrollers, etc.)
        SHOOT, // Outtake gamepiece with two rollers (flywheel with backrollers, etc.)
        DISABLED // No gamepiece interaction, motor count irrelevant
    }

    private double exitVelocity;
    private Rotation3d exitRotation;
    private Mode mode = Mode.DISABLED;

    public GamepieceZone(SimRobot robot) {
        // Optionally link to robot or state manager if needed
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        // Integrate with StateManager if needed
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

    public Mode getMode() {
        return mode;
    }
}
