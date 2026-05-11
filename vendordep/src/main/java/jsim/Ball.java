// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

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
   * @param positionMeters the new position in meters
   */
  public void setPosition(Vec3 positionMeters) {
    world.setBallPosition(ballIndex, positionMeters.x(), positionMeters.y(), positionMeters.z());
  }

  /**
   * Sets the ball's world-space position in meters.
   *
   * @param xMeters x position in meters
   * @param yMeters y position in meters
   * @param zMeters z position in meters
   */
  public void setPosition(double xMeters, double yMeters, double zMeters) {
    world.setBallPosition(ballIndex, xMeters, yMeters, zMeters);
  }

  /**
   * Sets the ball's world-space linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
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
   * @param positionMeters launch position in meters
   * @param velocityMps launch velocity in meters per second
   */
  public void shoot(Vec3 positionMeters, Vec3 velocityMps) {
    setPosition(positionMeters);
    setLinearVelocity(velocityMps);
  }

  /**
   * Gets the current world-space position in meters.
   *
   * @return the ball position
   */
  public Vec3 position() {
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
  public Vec3 linearVelocity() {
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
