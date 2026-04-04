package rensim.simulation.drivesims;

import java.util.Arrays;
import rensim.Vec3;
import rensim.simulation.SimulationOptions;

/**
 * Higher-level convenience layer that controls a {@link SwerveDriveSimulation}.
 */
public final class SelfControlledSwerveDriveSimulation {
  private final SwerveDriveSimulation drive;
  private final SwerveModuleSimulation[] modules;
  private double requestedVxMps;
  private double requestedVyMps;
  private double requestedOmegaRadPerSec;

  public SelfControlledSwerveDriveSimulation(SwerveDriveSimulation drive,
      SwerveModuleSimulation... modules) {
    if (modules.length != 4) {
      throw new IllegalArgumentException("Expected exactly 4 swerve modules");
    }
    this.drive = drive;
    this.modules = Arrays.copyOf(modules, modules.length);
  }

  public void runChassisSpeeds(double vxMps, double vyMps, double omegaRadPerSec) {
    requestedVxMps = vxMps;
    requestedVyMps = vyMps;
    requestedOmegaRadPerSec = omegaRadPerSec;
  }

  public void simulationSubTick(double dtSeconds, SimulationOptions options) {
    drive.runChassisSpeeds(requestedVxMps, requestedVyMps, requestedOmegaRadPerSec);
    for (SwerveModuleSimulation module : modules) {
      module.requestState(Math.hypot(requestedVxMps, requestedVyMps),
          Math.atan2(requestedVyMps, requestedVxMps));
      module.simulationSubTick(dtSeconds, options, drive.currentLinearVelocity());
    }
    drive.simulationSubTick(dtSeconds, options, modules);
  }

  public SwerveDriveSimulation drive() {
    return drive;
  }

  public Vec3[] moduleForces() {
    Vec3[] result = new Vec3[modules.length];
    for (int i = 0; i < modules.length; i++) {
      result[i] = modules[i].moduleForceWorldFrame(drive.robotMassKg());
    }
    return result;
  }
}