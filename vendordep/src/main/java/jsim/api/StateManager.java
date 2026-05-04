package jsim.api;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import jsim.field.FieldConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * System Rules:
 * - Single source of truth for all simulation state.
 * - Only system allowed to mutate FieldState<T>.
 * - External APIs are read/write proxies ONLY through defined methods.
 */
public class StateManager {
    private static final StateManager INSTANCE = new StateManager();

    private final Map<RobotID, FieldState<SimRobot.RobotState>> robotStates = new HashMap<>();

    private StateManager() {}

    public static StateManager getInstance() {
        return INSTANCE;
    }

    /**
     * Loads JSON field definition and builds all FieldElements and collision zones.
     */
    public void initializeField(FieldConfig config) {
        // Build collision zones based on parsed config schemas
    }

    /**
     * Creates a robot instance, assigns starting pose, and registers it in the simulation.
     */
    public SimRobot initializeRobot(RobotID id, Pose2d startingPose, Translation2d[] frameVertices) {
        SimRobot.RobotState internalState = new SimRobot.RobotState();
        internalState.pose = startingPose;
        
        FieldState<SimRobot.RobotState> stateRef = new FieldState<>(internalState);
        robotStates.put(id, stateRef);

        return new SimRobot(id, stateRef);
    }
}
