package jsim.api.gamepieces;

import jsim.api.GamePieceState;
import jsim.api.GamePiecePhysics;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * Implementation of 2026 Game Piece Physics bounds
 */
public class Fuel2026 extends GamePieceState {

    public Fuel2026() {
        super(new GamePiecePhysics());
    }

    /**
     * Executes a physical shot.
     */
    public void shoot(Translation3d relativeStart, double timeOfFlightMs, Rotation3d exitAngle) {
        this.lifecycle = Lifecycle.ACTIVE;
        this.physics.linearVelocity = new Translation3d(1.0, 0.0, 0.0); // Derived from angle and TOF
    }
}
