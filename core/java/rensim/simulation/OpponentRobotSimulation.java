package rensim.simulation;

import java.util.Objects;
import java.util.function.Consumer;
import rensim.simulation.drivesims.SwerveDriveSimulation;

/**
 * Wrapper for scripted/joystick-driven opponent robots in the simulation world.
 */
public final class OpponentRobotSimulation implements SimulatedArena.Simulatable {
  private final SwerveDriveSimulation drive;
  private Consumer<SwerveDriveSimulation> behavior = d -> {};

  public OpponentRobotSimulation(SwerveDriveSimulation drive) {
    this.drive = Objects.requireNonNull(drive);
  }

  public OpponentRobotSimulation withBehavior(Consumer<SwerveDriveSimulation> behavior) {
    this.behavior = Objects.requireNonNull(behavior);
    return this;
  }

  public SwerveDriveSimulation drive() {
    return drive;
  }

  @Override
  public void simulationSubTick(int subTickNum) {
    behavior.accept(drive);
  }
}
