package rensim.simulation.drivesims;

import rensim.simulation.motorsims.SimMotorConfigs;

/**
 * Commercial off-the-shelf module presets for swerve simulation.
 */
public final class COTS {
  private COTS() {}

  public static SwerveModuleSimulationConfig ofSwerveX2(double wheelCof, int gearRatioLevel) {
    double driveRatio = switch (gearRatioLevel) {
      case 1 -> 6.75;
      case 2 -> 6.23;
      case 3 -> 5.79;
      default -> 6.75;
    };
    return new SwerveModuleSimulationConfig(
        SimMotorConfigs.falcon500Like(driveRatio),
        SimMotorConfigs.falcon500Like(12.8),
        0.0508,
        wheelCof,
        260.0);
  }

  public static SwerveModuleSimulationConfig ofThriftySwerve(double wheelCof, int gearRatioLevel) {
    double driveRatio = switch (gearRatioLevel) {
      case 1 -> 6.75;
      case 2 -> 6.23;
      case 3 -> 5.79;
      case 4 -> 6.0;
      case 5 -> 5.54;
      case 6 -> 5.14;
      default -> 6.75;
    };
    return new SwerveModuleSimulationConfig(
        SimMotorConfigs.falcon500Like(driveRatio),
        SimMotorConfigs.falcon500Like(25.0),
        0.0508,
        wheelCof,
        250.0);
  }
}