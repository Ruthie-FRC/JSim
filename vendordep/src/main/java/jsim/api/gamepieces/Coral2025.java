package jsim.api.gamepieces;

import jsim.api.GamePieceState;
import jsim.api.GamePiecePhysics;
import edu.wpi.first.math.geometry.Pose3d;

/**
 * Implementation of 2025 Game Piece layout bounds
 */
public class Coral2025 extends GamePieceState {

    public Coral2025() {
        super(new GamePiecePhysics());
    }

    /**
     * Executes placing the piece onto a branch node
     */
    public void place(Pose3d branchTarget) {
        this.position = branchTarget;
        this.lifecycle = Lifecycle.INTERACTING; // Before resolving definitively securely
    }
}
