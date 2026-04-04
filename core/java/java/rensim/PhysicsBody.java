package rensim;

/**
 * Handle for a body managed by {@link PhysicsWorld}.
 */
public final class PhysicsBody {
  private final PhysicsWorld world;
  private final int bodyIndex;
  private final int bodyId;

  PhysicsBody(PhysicsWorld world, int bodyIndex, int bodyId) {
    this.world = world;
    this.bodyIndex = bodyIndex;
    this.bodyId = bodyId;
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
   * Gets the unique body identifier used for tracking across frames.
   *
   * @return the body ID
   */
  public int id() {
    return bodyId;
  }

  /**
   * Sets the body's world-space position in meters.
   *
   * @param positionMeters the new position in meters
   */
  public void setPosition(Vec3 positionMeters) {
    world.setBodyPosition(bodyIndex, positionMeters);
  }

  /**
   * Sets the body's linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
  public void setLinearVelocity(Vec3 velocityMps) {
    world.setBodyLinearVelocity(bodyIndex, velocityMps);
  }

  /**
   * Enables or disables gravity for this body.
   *
   * @param enabled true to enable gravity, false to disable it
   */
  public void setGravityEnabled(boolean enabled) {
    world.setBodyGravityEnabled(bodyIndex, enabled);
  }

  /**
   * Returns the body's current world-space position in meters.
   *
   * @return the current position in meters
   */
  public Vec3 position() {
    return world.bodyPosition(bodyIndex);
  }

  /**
   * Returns the body's current linear velocity in meters per second.
   *
   * @return the current linear velocity in meters per second
   */
  public Vec3 linearVelocity() {
    return world.bodyLinearVelocity(bodyIndex);
  }
}