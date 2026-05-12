// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

/**
 * Immutable 3D linear velocity in meters per second.
 *
 * @param xMetersPerSecond the x component in meters per second
 * @param yMetersPerSecond the y component in meters per second
 * @param zMetersPerSecond the z component in meters per second
 */
public record LinearVelocity3d(
    double xMetersPerSecond, double yMetersPerSecond, double zMetersPerSecond) {
  /** Zero velocity with all components set to 0.0 m/s. */
  public static final LinearVelocity3d ZERO = new LinearVelocity3d(0.0, 0.0, 0.0);

  /**
   * Returns the velocity components as a length-3 array.
   *
   * @return a new array containing {vx, vy, vz}
   */
  public double[] toArray() {
    return new double[] {xMetersPerSecond, yMetersPerSecond, zMetersPerSecond};
  }

  /**
   * Builds a velocity from a length-3 array.
   *
   * @param values array containing {vx, vy, vz}
   * @return the velocity record
   */
  public static LinearVelocity3d fromArray(double[] values) {
    if (values.length < 3) {
      throw new IllegalArgumentException("Velocity array must contain at least 3 values");
    }
    return new LinearVelocity3d(values[0], values[1], values[2]);
  }
}
