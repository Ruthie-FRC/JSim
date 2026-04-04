package rensim.simulation.drivesims;

import frcsim_physics.RigidBody;
import frcsim_physics.SwerveModel;
import rensim.Vec3;
import rensim.simulation.Pose2;
import rensim.simulation.SimulationOptions;

/**
 * Maple-style swerve drive simulation wrapper on top of RenSim rigid-body APIs.
 */
public final class SwerveDriveSimulation {
  private final DriveTrainSimulationConfig config;
  private final RigidBody rigidBody;
  private final SwerveModel model;

  /**
   * Creates a swerve simulation wrapper from config and rigid-body host.
   */
  public SwerveDriveSimulation(DriveTrainSimulationConfig config, RigidBody rigidBody) {
    this.config = config;
    this.rigidBody = rigidBody;
    this.model = new SwerveModel(config.trackLengthXMeters(), config.trackWidthYMeters(),
        config.maxWheelSpeedMps());
  }

  /**
   * Requests chassis velocity setpoint for this simulation tick.
   */
  public void runChassisSpeeds(double vxMps, double vyMps, double omegaRadPerSecond) {
    model.applyToBody(rigidBody, new SwerveModel.ChassisCommand(vxMps, vyMps, omegaRadPerSecond));
  }

  /**
   * Advances drivetrain-specific simulation state.
   */
  public void simulationSubTick(double dtSeconds) {
    rigidBody.advance(dtSeconds);
  }

  /**
   * Advances drivetrain with module force aggregation and configurable friction.
   */
  public void simulationSubTick(double dtSeconds, SimulationOptions options,
      SwerveModuleSimulation[] modules) {
    Vec3 totalAccel = Vec3.ZERO;
    for (SwerveModuleSimulation module : modules) {
      totalAccel = totalAccel.add(module.moduleForceWorldFrame(config.robotMassKg()));
    }

    Vec3 vel = rigidBody.linearVelocityMps().add(totalAccel.scale(dtSeconds));
    double drag = Math.exp(-options.friction().linearDragPerSecond() * dtSeconds);
    vel = vel.scale(drag);
    if (vel.norm() <= options.tolerances().velocityDeadbandMps()) {
      vel = Vec3.ZERO;
    }
    rigidBody.setLinearVelocityMps(new Vec3(vel.x(), vel.y(), 0.0));

    Vec3 omega = rigidBody.angularVelocityRadPerSec();
    double angularDrag = Math.exp(-options.friction().angularDragPerSecond() * dtSeconds);
    Vec3 omegaNext = omega.scale(angularDrag);
    if (Math.abs(omegaNext.z()) <= options.tolerances().velocityDeadbandMps()) {
      omegaNext = new Vec3(0.0, 0.0, 0.0);
    }
    rigidBody.setAngularVelocityRadPerSec(omegaNext);
    rigidBody.advance(dtSeconds);
  }

  /**
   * Gets current simulated pose.
   */
  public Pose2 pose() {
    var p = rigidBody.positionMeters();
    return new Pose2(p.x(), p.y(), 0.0);
  }

  /**
   * Gets max modeled chassis linear speed.
   */
  public double maxLinearVelocityMps() {
    return config.maxWheelSpeedMps();
  }

  /**
   * Gets configured wheelbase radius proxy.
   */
  public double driveBaseRadiusMeters() {
    double halfL = config.trackLengthXMeters() * 0.5;
    double halfW = config.trackWidthYMeters() * 0.5;
    return Math.hypot(halfL, halfW);
  }

  /**
   * Gets current linear velocity from the rigid body.
   */
  public Vec3 currentLinearVelocity() {
    return rigidBody.linearVelocityMps();
  }

  /**
   * Gets configured robot mass.
   */
  public double robotMassKg() {
    return config.robotMassKg();
  }
}