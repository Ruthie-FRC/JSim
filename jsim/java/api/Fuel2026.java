package api;

/**
 * Represents a 2026 Fuel game piece.
 * Integrates vector, quaternion, and matrix math for simulation.
 */
public class Fuel2026 extends GamePieceState {
    /**
     * Constructs a Fuel2026 game piece.
     */
    public Fuel2026() { super(GamePieceType.FUEL); }

    /**
     * Simulates shooting the fuel game piece using vector and quaternion math.
     * @param relativeStart The relative start position.
     * @param timeOfFlight The time of flight.
     * @param exitAngle The exit angle (as Rotation3d, converted to Quaternion).
     */
    public void shoot(Translation3d relativeStart, double timeOfFlight, Rotation3d exitAngle) {
        setExitAngle(exitAngle);

        // Convert Translation3d to Vector3
    // Vector3 start = new Vector3(relativeStart.x, relativeStart.y, relativeStart.z); // Unused

        // Convert Rotation3d to Quaternion (assuming ZYX order)
        Quaternion q = quaternionFromRotation3d(exitAngle);

        // Example: rotate a velocity vector by the exit angle
        Vector3 velocity = new Vector3(1, 0, 0); // placeholder for actual velocity
        Vector3 rotatedVelocity = q.rotate(velocity);

        // Example: use a matrix for further transformation (identity for now)
        Matrix3 m = Matrix3.identity();
    // Vector3 transformed = m.multiply(rotatedVelocity); // Unused

        // Store or use the result as needed (for now, just a placeholder)
        // setExitVelocity(new Translation3d(transformed.x, transformed.y, transformed.z));
    }

    /**
     * Converts a Rotation3d (roll, pitch, yaw) to a Quaternion (ZYX order).
     */
    private static Quaternion quaternionFromRotation3d(Rotation3d r) {
        // ZYX Euler to Quaternion
        double cy = Math.cos(r.yaw * 0.5);
        double sy = Math.sin(r.yaw * 0.5);
        double cp = Math.cos(r.pitch * 0.5);
        double sp = Math.sin(r.pitch * 0.5);
        double cr = Math.cos(r.roll * 0.5);
        double sr = Math.sin(r.roll * 0.5);

        double w = cr * cp * cy + sr * sp * sy;
        double x = sr * cp * cy - cr * sp * sy;
        double y = cr * sp * cy + sr * cp * sy;
        double z = cr * cp * sy - sr * sp * cy;
        return new Quaternion(w, x, y, z);
    }
}
