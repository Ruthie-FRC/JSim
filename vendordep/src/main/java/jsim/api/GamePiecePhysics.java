package jsim.api;

import edu.wpi.first.math.geometry.Translation3d;

/**
 * Underlying physics state matching standard trajectory model for FlyingGamePiece
 */
public class GamePiecePhysics {
    public Translation3d linearVelocity = new Translation3d();
    public Translation3d angularVelocity = new Translation3d();

    public void applyMagnusEffect() {
        // Core physics binding hook layout mapping
    }
}
