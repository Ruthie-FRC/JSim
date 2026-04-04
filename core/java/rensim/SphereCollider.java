package rensim;

/**
 * Sphere collider used by the Java-side starter collision pass.
 *
 * @param radiusMeters collider radius in meters
 * @param restitution coefficient of restitution in [0, 1]
 */
public record SphereCollider(double radiusMeters, double restitution) {
  /**
   * Creates a sphere collider and validates parameters.
   */
  public SphereCollider {
    if (!(radiusMeters > 0.0)) {
      throw new IllegalArgumentException("radiusMeters must be > 0");
    }
    if (restitution < 0.0 || restitution > 1.0) {
      throw new IllegalArgumentException("restitution must be in [0, 1]");
    }
  }
}