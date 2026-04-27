package jsim.driver.WPILibClones;

public class Translation3d {
    private double x, y, z;
    public Translation3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
}