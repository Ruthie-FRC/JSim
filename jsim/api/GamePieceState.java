package jsim.api;

import jsim.api.GamePieceType;
import jsim.driver.WPILibClones.Rotation3d;

public class GamePieceState {
    private GamePieceType type;
    private double velocity;
    private Rotation3d rotation;

    public GamePieceState(GamePieceType type) {
        this.type = type;
    }

    public GamePieceType getType() {
        return type;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public void setRotation(Rotation3d rotation) {
        this.rotation = rotation;
    }

    public double getVelocity() {
        return velocity;
    }

    public Rotation3d getRotation() {
        return rotation;
    }
}
