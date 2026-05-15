// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.Distance;

/**
 * Handle for a ball managed by {@link PhysicsWorld}.
 */
public final class Ball {
  private final PhysicsWorld world;
  private final int ballIndex;

  Ball(PhysicsWorld world, int ballIndex) {
    this.world = world;
    this.ballIndex = ballIndex;
  }

  /**
   * Gets the native ball index for this ball.
   *
   * @return the native ball index
   */
  public int ballIndex() {
    return ballIndex;
  }

  /**
   * Sets the ball's world-space position in meters.
   *
   * @param pose the new position (only translation component is used)
   */
  public void setPosition(Pose3d pose) {
    Translation3d translation = pose.getTranslation();
    world.setBallPosition(ballIndex, translation.getX(), translation.getY(), translation.getZ());
  }

  /**
   * Sets the ball's world-space position in meters.
   *
   * @param positionMeters the new position in meters
   */
  public void setPosition(Translation3d positionMeters) {
    world.setBallPosition(ballIndex, positionMeters.getX(), positionMeters.getY(), positionMeters.getZ());
  }

  /**
   * Sets the ball's world-space position in meters.
   *
   * @param positionMeters the new position in meters
   * @deprecated use setPosition(Pose3d), setPosition(Translation3d), or setPosition(Distance, Distance, Distance)
   */
  @Deprecated(forRemoval = false)
  public void setPosition(Vec3 positionMeters) {
    world.setBallPosition(ballIndex, positionMeters.x(), positionMeters.y(), positionMeters.z());
  }

  /**
   * Sets the ball's world-space position.
   *
   * @param x x position
   * @param y y position
   * @param z z position
   */
  public void setPosition(Distance x, Distance y, Distance z) {
    world.setBallPosition(ballIndex, x.in(Meters), y.in(Meters), z.in(Meters));
  }

  /**
   * Sets the ball's world-space position in meters.
   *
   * @param xMeters x position in meters
   * @param yMeters y position in meters
   * @param zMeters z position in meters
   */
  void setPosition(double xMeters, double yMeters, double zMeters) {
    world.setBallPosition(ballIndex, xMeters, yMeters, zMeters);
  }

  /**
   * Sets the ball's world-space linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
  public void setLinearVelocity(LinearVelocity3d velocityMps) {
    world.setBallLinearVelocity(
        ballIndex,
        velocityMps.getVxMetersPerSecond(),
        velocityMps.getVyMetersPerSecond(),
        velocityMps.getVzMetersPerSecond());
  }

  /**
   * Sets the ball's world-space linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   * @deprecated use setLinearVelocity(LinearVelocity3d)
   */
  @Deprecated(forRemoval = false)
  public void setLinearVelocity(Vec3 velocityMps) {
    world.setBallLinearVelocity(ballIndex, velocityMps.x(), velocityMps.y(), velocityMps.z());
  }

  /**
   * Sets the ball's world-space linear velocity in meters per second.
   *
   * @param vxMetersPerSecond x velocity in meters per second
   * @param vyMetersPerSecond y velocity in meters per second
   * @param vzMetersPerSecond z velocity in meters per second
   */
  public void setLinearVelocity(
      double vxMetersPerSecond, double vyMetersPerSecond, double vzMetersPerSecond) {
    world.setBallLinearVelocity(ballIndex, vxMetersPerSecond, vyMetersPerSecond, vzMetersPerSecond);
  }

  /**
   * Convenience method to set both position and launch velocity.
   *
   * @param pose launch position (only translation component is used)
   * @param velocityMps launch velocity in meters per second
   */
  public void shoot(Pose3d pose, LinearVelocity3d velocityMps) {
    setPosition(pose);
    setLinearVelocity(velocityMps);
  }

  /**
   * Convenience method to set both position and launch velocity.
   *
   * @param positionMeters launch position in meters
   * @param velocityMps launch velocity in meters per second
   */
  public void shoot(Translation3d positionMeters, LinearVelocity3d velocityMps) {
    setPosition(positionMeters);
    setLinearVelocity(velocityMps);
  }

  /**
   * Convenience method to set both position and launch velocity.
   *
   * @param positionMeters launch position in meters
   * @param velocityMps launch velocity in meters per second
   * @deprecated use shoot(Pose3d, LinearVelocity3d) or shoot(Translation3d, LinearVelocity3d)
   */
  @Deprecated(forRemoval = false)
  public void shoot(Vec3 positionMeters, Vec3 velocityMps) {
    setPosition(positionMeters);
    setLinearVelocity(velocityMps);
  }

  /**
   * Gets the current world-space position in meters.
   *
   * @return the ball position
   */
  public Pose3d position() {
    return world.getBallPosition(ballIndex);
  }

  /**
   * Gets the current world-space linear velocity in meters per second.
   *
   * @return the ball linear velocity
   */
  public LinearVelocity3d linearVelocity() {
    return world.getBallLinearVelocity(ballIndex);
  }
}
