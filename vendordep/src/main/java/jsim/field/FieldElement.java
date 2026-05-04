package jsim.field;

import edu.wpi.first.math.geometry.Pose3d;

/**
 * Basic block of the JSim environment (HUB, DEPOT, HP_STATION)
 */
public class FieldElement {
    public enum Type {
        HUB, 
        DEPOT, 
        HP_STATION,
        OBSTACLE
    }

    public Type type;
    public Pose3d position;
    // public Composition composition; // Future layout definition structure
}
