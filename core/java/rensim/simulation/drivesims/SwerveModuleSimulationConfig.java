package rensim.simulation.drivesims;

import rensim.simulation.motorsims.SimMotorConfigs;

/**
 * Configuration for one simulated swerve module.
 */
public final class SwerveModuleSimulationConfig {
  private final SimMotorConfigs driveMotorConfigs;
  private final SimMotorConfigs steerMotorConfigs;
  private final double wheelRadiusMeters;
  private final double wheelsCoefficientOfFriction;
  private final double maxGripForceNewtons;

  public SwerveModuleSimulationConfig(SimMotorConfigs driveMotorConfigs,
      SimMotorConfigs steerMotorConfigs, double wheelRadiusMeters,
      double wheelsCoefficientOfFriction, double maxGripForceNewtons) {
    if (!(wheelRadiusMeters > 0.0) || !(wheelsCoefficientOfFriction > 0.0)
        || !(maxGripForceNewtons > 0.0)) {
      throw new IllegalArgumentException("Invalid module configuration values");
    }
    this.driveMotorConfigs = driveMotorConfigs;
    this.steerMotorConfigs = steerMotorConfigs;
    this.wheelRadiusMeters = wheelRadiusMeters;
    this.wheelsCoefficientOfFriction = wheelsCoefficientOfFriction;
    this.maxGripForceNewtons = maxGripForceNewtons;
  }

  public static SwerveModuleSimulationConfig defaults() {
    return new SwerveModuleSimulationConfig(
        SimMotorConfigs.falcon500Like(6.12),
        SimMotorConfigs.falcon500Like(12.8),
        0.0508,
        1.2,
        250.0);
  }

  public SimMotorConfigs driveMotorConfigs() {
    return driveMotorConfigs;
  }

  public SimMotorConfigs steerMotorConfigs() {
    return steerMotorConfigs;
  }

  public double wheelRadiusMeters() {
    return wheelRadiusMeters;
  }

  public double wheelsCoefficientOfFriction() {
    return wheelsCoefficientOfFriction;
  }

  public double maxGripForceNewtons() {
    return maxGripForceNewtons;
  }
}