package rensim;

/**
 * Read-only view of a physics body's state.
 *
 * <p>Exposes all necessary state for rendering, analysis, and debugging without
 * allowing modification through this interface.
 */
public interface BodyStateView {
  /**
   * Returns the body's unique identifier.
   *
   * @return body ID
   */
  int id();

  /**
   * Returns the body mass in kilograms.
   *
   * @return mass in kilograms
   */
  double massKg();

  /**
   * Returns the world-space position in meters.
   *
   * @return position in meters
   */
  Vec3 position();

  /**
   * Returns the linear velocity in meters per second.
   *
   * @return linear velocity in meters per second
   */
  Vec3 linearVelocity();

  /**
   * Returns whether gravity is enabled for this body.
   *
   * @return true if gravity enabled
   */
  boolean gravityEnabled();
}
