package jsim.driver.WPILibClones;

public class Pose2d {
    private double x, y, theta;
    public Pose2d(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getTheta() { return theta; }
}
