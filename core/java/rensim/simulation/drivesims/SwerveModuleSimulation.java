package rensim.simulation.drivesims;

import rensim.Vec3;
import rensim.simulation.SimulationOptions;
import rensim.simulation.motorsims.MapleMotorSim;
import rensim.simulation.motorsims.SimulatedMotorController;

/**
 * Simulation for a single swerve module with drive and steer motor loops.
 */
public final class SwerveModuleSimulation {
  /**
   * Lightweight module state.
   */
  public record ModuleState(double speedMps, double angleRad) {}

  private final SwerveModuleSimulationConfig config;
  private final MapleMotorSim driveMotorSim;
  private final MapleMotorSim steerMotorSim;

  private final SimulatedMotorController.GenericMotorController driveController;
  private final SimulatedMotorController.GenericMotorController steerController;

  private double desiredSpeedMps;
  private double desiredAngleRad;
  private double wheelPositionRad;
  private double wheelSpeedRadPerSec;
  private double steerAngleRad;

  public SwerveModuleSimulation(SwerveModuleSimulationConfig config) {
    this.config = config;
    this.driveMotorSim = new MapleMotorSim(config.driveMotorConfigs());
    this.steerMotorSim = new MapleMotorSim(config.steerMotorConfigs());
    this.driveController = driveMotorSim.useSimpleDCMotorController();
    this.steerController = steerMotorSim.useSimpleDCMotorController();
  }

  public void requestState(double speedMps, double angleRad) {
    this.desiredSpeedMps = speedMps;
    this.desiredAngleRad = angleRad;
  }

  public void simulationSubTick(double dtSeconds, SimulationOptions options,
      Vec3 moduleGroundVelocityWorldFrame) {
    double velocityDeadband = options.tolerances().velocityDeadbandMps();

    double targetWheelRadPerSec = desiredSpeedMps / config.wheelRadiusMeters();
    double wheelError = targetWheelRadPerSec - wheelSpeedRadPerSec;
    double driveVoltageCmd = clamp(2.2 * wheelError, -12.0, 12.0);
    driveController.requestVoltage(driveVoltageCmd);

    double steerError = wrapAngle(desiredAngleRad - steerAngleRad);
    double steerVoltageCmd = clamp(8.0 * steerError, -12.0, 12.0);
    steerController.requestVoltage(steerVoltageCmd);

    driveMotorSim.update(dtSeconds, velocityDeadband);
    steerMotorSim.update(dtSeconds, velocityDeadband);

    wheelSpeedRadPerSec = driveMotorSim.getEncoderVelocityRadPerSec();
    wheelPositionRad = driveMotorSim.getEncoderPositionRad();
    steerAngleRad = steerMotorSim.getEncoderPositionRad();

    double floorProjection = moduleGroundVelocityWorldFrame.x() * Math.cos(steerAngleRad)
        + moduleGroundVelocityWorldFrame.y() * Math.sin(steerAngleRad);
    double slip = wheelSpeedRadPerSec * config.wheelRadiusMeters() - floorProjection;
    if (Math.abs(slip) <= velocityDeadband) {
      wheelSpeedRadPerSec = floorProjection / config.wheelRadiusMeters();
    }
  }

  public Vec3 moduleForceWorldFrame(double robotMassKg) {
    double driveTorque = config.driveMotorConfigs().calculateTorque(driveMotorSim.getStatorCurrentAmps());
    double idealForce = driveTorque / config.wheelRadiusMeters();
    double gripLimited = clamp(idealForce, -config.maxGripForceNewtons(), config.maxGripForceNewtons());
    return new Vec3(Math.cos(steerAngleRad) * gripLimited / robotMassKg,
        Math.sin(steerAngleRad) * gripLimited / robotMassKg, 0.0);
  }

  public ModuleState currentState() {
    return new ModuleState(wheelSpeedRadPerSec * config.wheelRadiusMeters(), steerAngleRad);
  }

  public double driveStatorCurrentAmps() {
    return driveMotorSim.getStatorCurrentAmps();
  }

  public double steerStatorCurrentAmps() {
    return steerMotorSim.getStatorCurrentAmps();
  }

  public double driveEncoderPositionRad() {
    return wheelPositionRad;
  }

  public double driveEncoderVelocityRadPerSec() {
    return wheelSpeedRadPerSec;
  }

  public double steerEncoderPositionRad() {
    return steerAngleRad;
  }

  private static double wrapAngle(double angle) {
    double result = angle;
    while (result > Math.PI) {
      result -= 2.0 * Math.PI;
    }
    while (result < -Math.PI) {
      result += 2.0 * Math.PI;
    }
    return result;
  }

  private static double clamp(double value, double min, double max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }
}