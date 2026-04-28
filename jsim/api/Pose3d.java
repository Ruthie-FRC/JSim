package api;

public class Pose3d {
    public final double x, y, z, roll, pitch, yaw;
    public Pose3d(double x, double y, double z, double roll, double pitch, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
