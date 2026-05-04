package jsim.api;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * Tracks the lifecycle and data of a standard game piece in the simulation.
 */
public class GamePieceState {

    public enum Lifecycle {
        SPAWNED,
        ACTIVE,
        INTERACTING,
        RESOLVED
    }

    public Lifecycle lifecycle = Lifecycle.SPAWNED;
    public Pose3d position = new Pose3d();
    public GamePiecePhysics physics;

    public GamePieceState(GamePiecePhysics physicsType) {
        this.physics = physicsType;
    }

    /**
     * Defines valid interaction volume for intake.
     */
    public void intakeZone(Translation3d[] intakeArea) {
        // Defines the polygon bounds in 3d space for intaking
    }

    /**
     * Ejects/spawns a piece based on parameters mapping.
     */
    public static FieldState<GamePieceState> spawn(Rotation3d exitAngle, Translation3d exitVelocity, Translation2d robotOffsetStart) {
        GamePieceState state = new GamePieceState(new GamePiecePhysics());
        state.lifecycle = Lifecycle.ACTIVE;
        // In reality, this links back into the StateManager
        return new FieldState<>(state);
    }
}
