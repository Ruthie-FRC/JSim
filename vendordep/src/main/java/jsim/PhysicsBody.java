// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.Distance;

/**
 * Handle for a body managed by {@link PhysicsWorld}.
 */
public final class PhysicsBody {
  private final PhysicsWorld world;
  private final int bodyIndex;

  PhysicsBody(PhysicsWorld world, int bodyIndex) {
    this.world = world;
    this.bodyIndex = bodyIndex;
  }

  /**
   * Gets the native body index for this body.
   *
   * @return the native body index
   */
  public int bodyIndex() {
    return bodyIndex;
  }

  /**
   * Sets the body's world-space position in meters.
   *
   * @param pose the new position (only translation component is used)
   */
  public void setPosition(Pose3d pose) {
    Translation3d translation = pose.getTranslation();
    world.setBodyPosition(bodyIndex, translation.getX(), translation.getY(), translation.getZ());
  }

  /**
   * Sets the body's world-space position.
   *
   * @param x x position
   * @param y y position
   * @param z z position
   */
  public void setPosition(Distance x, Distance y, Distance z) {
    world.setBodyPosition(bodyIndex, x.in(Meters), y.in(Meters), z.in(Meters));
  }

  /**
   * Sets the body's linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
  public void setLinearVelocity(LinearVelocity3d velocityMps) {
    world.setBodyLinearVelocity(
        bodyIndex,
        velocityMps.getVxMetersPerSecond(),
        velocityMps.getVyMetersPerSecond(),
        velocityMps.getVzMetersPerSecond());
  }

  /**
   * Sets the body's linear velocity in meters per second.
   *
   * @param vxMetersPerSecond x velocity in meters per second
   * @param vyMetersPerSecond y velocity in meters per second
   * @param vzMetersPerSecond z velocity in meters per second
   */
  public void setLinearVelocity(
      double vxMetersPerSecond, double vyMetersPerSecond, double vzMetersPerSecond) {
    world.setBodyLinearVelocity(
        bodyIndex, vxMetersPerSecond, vyMetersPerSecond, vzMetersPerSecond);
  }

  /**
   * Enables or disables gravity for this body.
   *
   * @param enabled true to enable gravity
   */
  public void setGravityEnabled(boolean enabled) {
    world.setBodyGravityEnabled(bodyIndex, enabled);
  }

  /**
   * Sets an approximate spherical collision/body shape for this body.
   *
   * @param radiusMeters sphere radius in meters
   */
  public void setCollisionSphere(double radiusMeters) {
    world.setBodyAerodynamicSphere(bodyIndex, radiusMeters, 0.0);
  }

  /**
   * Sets an approximate box collision/body shape for this body.
   *
   * @param xMeters box x dimension in meters
   * @param yMeters box y dimension in meters
   * @param zMeters box z dimension in meters
   */
  public void setCollisionBox(double xMeters, double yMeters, double zMeters) {
    world.setBodyAerodynamicBox(bodyIndex, xMeters, yMeters, zMeters, 0.0);
  }

  /**
   * Sets broad-phase collision filtering for this body.
   *
   * @param collisionLayerBits body layer bitmask
   * @param collisionMaskBits body mask bitmask
   */
  public void setCollisionFilter(int collisionLayerBits, int collisionMaskBits) {
    world.setBodyCollisionFilter(bodyIndex, collisionLayerBits, collisionMaskBits);
  }

  /**
   * Gets the current world-space position in meters.
   *
   * @return the body position
   */
  public Pose3d position() {
    return world.getBodyPosition(bodyIndex);
  }

  /**
   * Gets the current world-space position in meters.
   *
   * @return a length-3 array containing {x, y, z}
   */
  public double[] positionArray() {
    return world.getBodyPositionArray(bodyIndex);
  }

  /**
   * Gets the current world-space linear velocity in meters per second.
   *
   * @return the body linear velocity
   */
  public LinearVelocity3d linearVelocity() {
    return world.getBodyLinearVelocity(bodyIndex);
  }

  /**
   * Gets the current world-space linear velocity in meters per second.
   *
   * @return a length-3 array containing {vx, vy, vz}
   */
  public double[] linearVelocityArray() {
    return world.getBodyLinearVelocityArray(bodyIndex);
  }

}
