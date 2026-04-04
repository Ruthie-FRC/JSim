package rensim;

/**
 * Immutable view over rigid-body state used for rendering and telemetry.
 */
public interface BodyStateView {
  /**
   * Body identifier stable within a world instance.
   *
   * @return body id
   */
  int id();

  /**
   * Body mass in kilograms.
   *
   * @return body mass in kilograms
   */
  double massKg();

  /**
   * World-space body position in meters.
   *
   * @return position in meters
   */
  Vec3 position();

  /**
   * World-space linear velocity in meters per second.
   *
   * @return linear velocity in meters per second
   */
  Vec3 linearVelocity();

  /**
   * Whether gravity is enabled for this body.
   *
   * @return true when gravity is enabled
   */
  boolean gravityEnabled();
}