// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import edu.wpi.first.math.geometry.Pose3d;

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
   * Sets the ball's world pose.
   *
   * @param positionMeters the new pose
   */
  public void setPosition(Pose3d positionMeters) {
    world.setBallPosition(ballIndex, positionMeters);
  }

  /**
   * Sets the ball's world-space linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
  public void setLinearVelocity(LinearVelocity3d velocityMps) {
    world.setBallLinearVelocity(
      ballIndex,
      velocityMps.xMetersPerSecond(),
      velocityMps.yMetersPerSecond(),
      velocityMps.zMetersPerSecond());
  }

  /**
   * Sets the ball's world-space linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
  public void setLinearVelocity(Vec3 velocityMps) {
    setLinearVelocity(new LinearVelocity3d(velocityMps.x(), velocityMps.y(), velocityMps.z()));
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
   * @param positionMeters launch pose
   * @param velocityMps launch velocity in meters per second
   */
  public void shoot(Pose3d positionMeters, LinearVelocity3d velocityMps) {
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
   * Gets the current world-space position in meters.
   *
   * @return a length-3 array containing {x, y, z}
   */
  public double[] positionArray() {
    return world.getBallPositionArray(ballIndex);
  }

  /**
   * Gets the current world-space linear velocity in meters per second.
   *
   * @return the ball linear velocity
   */
  public LinearVelocity3d linearVelocity() {
    return world.getBallLinearVelocity(ballIndex);
  }

  /**
   * Gets the current world-space linear velocity in meters per second.
   *
   * @return a length-3 array containing {vx, vy, vz}
   */
  public double[] linearVelocityArray() {
    return world.getBallLinearVelocityArray(ballIndex);
  }
}
