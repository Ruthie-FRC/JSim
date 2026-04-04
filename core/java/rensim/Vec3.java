package rensim;

/**
 * Immutable 3D vector used for positions, velocities, and accelerations.
 *
 * @param x the x component
 * @param y the y component
 * @param z the z component
 */
public record Vec3(double x, double y, double z) {
  /** Zero vector with all components set to 0.0. */
  public static final Vec3 ZERO = new Vec3(0.0, 0.0, 0.0);

  /**
   * Adds another vector to this vector.
   *
   * @param other vector to add
   * @return summed vector
   */
  public Vec3 add(Vec3 other) {
    return new Vec3(x + other.x, y + other.y, z + other.z);
  }

  /**
   * Subtracts another vector from this vector.
   *
   * @param other vector to subtract
   * @return difference vector
   */
  public Vec3 subtract(Vec3 other) {
    return new Vec3(x - other.x, y - other.y, z - other.z);
  }

  /**
   * Scales this vector by a scalar value.
   *
   * @param scalar scalar multiplier
   * @return scaled vector
   */
  public Vec3 scale(double scalar) {
    return new Vec3(x * scalar, y * scalar, z * scalar);
  }

  /**
   * Computes the dot product with another vector.
   *
   * @param other vector to dot with
   * @return dot product value
   */
  public double dot(Vec3 other) {
    return x * other.x + y * other.y + z * other.z;
  }

  /**
   * Computes squared magnitude of this vector.
   *
   * @return squared magnitude
   */
  public double normSquared() {
    return x * x + y * y + z * z;
  }

  /**
   * Computes magnitude of this vector.
   *
   * @return Euclidean magnitude
   */
  public double norm() {
    return Math.sqrt(normSquared());
  }

  /**
   * Returns a normalized copy of this vector.
   *
   * @return unit-length vector or zero if input is near-zero
   */
  public Vec3 normalized() {
    double n = norm();
    if (n < 1.0e-12) {
      return ZERO;
    }
    return scale(1.0 / n);
  }
}