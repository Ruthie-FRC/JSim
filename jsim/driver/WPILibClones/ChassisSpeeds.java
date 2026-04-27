package jsim.driver.WPILibClones;

public class ChassisSpeeds {
    private double vx, vy, omega;
    public ChassisSpeeds(double vx, double vy, double omega) {
        this.vx = vx;
        this.vy = vy;
        this.omega = omega;
    }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getOmega() { return omega; }
}
