package rensim.simulation.drivesims;

/**
 * Configuration for swerve-drive simulation wrappers.
 */
public final class DriveTrainSimulationConfig {
  /** Lightweight 2D translation record. */
  public record Translation2(double xMeters, double yMeters) {}

  private double robotMassKg = 54.0;
  private double bumperLengthXMeters = 0.91;
  private double bumperWidthYMeters = 0.91;
  private double trackLengthXMeters = 0.52;
  private double trackWidthYMeters = 0.52;
  private double maxWheelSpeedMps = 5.5;
  private double wheelCoefficientOfFriction = 1.2;
  private double moduleSkidToleranceMps = 0.02;
  private Translation2[] moduleTranslations = new Translation2[] {
      new Translation2(0.26, 0.26),
      new Translation2(0.26, -0.26),
      new Translation2(-0.26, 0.26),
      new Translation2(-0.26, -0.26)
  };

  /**
   * Returns a default FRC-like drivetrain config.
   */
  public static DriveTrainSimulationConfig defaults() {
    return new DriveTrainSimulationConfig();
  }

  public DriveTrainSimulationConfig withRobotMassKg(double robotMassKg) {
    if (!(robotMassKg > 0.0)) {
      throw new IllegalArgumentException("robotMassKg must be > 0");
    }
    this.robotMassKg = robotMassKg;
    return this;
  }

  public DriveTrainSimulationConfig withBumperDimensions(double lengthXMeters, double widthYMeters) {
    if (!(lengthXMeters > 0.0) || !(widthYMeters > 0.0)) {
      throw new IllegalArgumentException("bumper dimensions must be > 0");
    }
    this.bumperLengthXMeters = lengthXMeters;
    this.bumperWidthYMeters = widthYMeters;
    return this;
  }

  public DriveTrainSimulationConfig withTrackLengthTrackWidth(double trackLengthXMeters,
      double trackWidthYMeters) {
    if (!(trackLengthXMeters > 0.0) || !(trackWidthYMeters > 0.0)) {
      throw new IllegalArgumentException("track dimensions must be > 0");
    }
    this.trackLengthXMeters = trackLengthXMeters;
    this.trackWidthYMeters = trackWidthYMeters;
    return this;
  }

  public DriveTrainSimulationConfig withMaxWheelSpeedMps(double maxWheelSpeedMps) {
    if (!(maxWheelSpeedMps > 0.0)) {
      throw new IllegalArgumentException("maxWheelSpeedMps must be > 0");
    }
    this.maxWheelSpeedMps = maxWheelSpeedMps;
    return this;
  }

  public DriveTrainSimulationConfig withWheelCoefficientOfFriction(double wheelCoefficientOfFriction) {
    if (!(wheelCoefficientOfFriction > 0.0)) {
      throw new IllegalArgumentException("wheelCoefficientOfFriction must be > 0");
    }
    this.wheelCoefficientOfFriction = wheelCoefficientOfFriction;
    return this;
  }

  public DriveTrainSimulationConfig withModuleSkidToleranceMps(double moduleSkidToleranceMps) {
    if (moduleSkidToleranceMps < 0.0) {
      throw new IllegalArgumentException("moduleSkidToleranceMps must be >= 0");
    }
    this.moduleSkidToleranceMps = moduleSkidToleranceMps;
    return this;
  }

  public DriveTrainSimulationConfig withCustomModuleTranslations(Translation2[] moduleTranslations) {
    if (moduleTranslations == null || moduleTranslations.length != 4) {
      throw new IllegalArgumentException("moduleTranslations must contain exactly 4 entries");
    }
    this.moduleTranslations = moduleTranslations.clone();
    return this;
  }

  public double robotMassKg() {
    return robotMassKg;
  }

  public double bumperLengthXMeters() {
    return bumperLengthXMeters;
  }

  public double bumperWidthYMeters() {
    return bumperWidthYMeters;
  }

  public double trackLengthXMeters() {
    return trackLengthXMeters;
  }

  public double trackWidthYMeters() {
    return trackWidthYMeters;
  }

  public double maxWheelSpeedMps() {
    return maxWheelSpeedMps;
  }

  public double wheelCoefficientOfFriction() {
    return wheelCoefficientOfFriction;
  }

  public double moduleSkidToleranceMps() {
    return moduleSkidToleranceMps;
  }

  public Translation2[] moduleTranslations() {
    return moduleTranslations.clone();
  }
}