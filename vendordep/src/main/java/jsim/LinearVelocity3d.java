// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.units.measure.LinearVelocity;
import java.util.Objects;

/**
 * Represents a 3D linear velocity vector with components in meters per second.
 * 
 * This class provides a semantic wrapper around velocity components to clearly
 * distinguish velocity vectors from position/translation vectors.
 */
public final class LinearVelocity3d {
  private final LinearVelocity vx;
  private final LinearVelocity vy;
  private final LinearVelocity vz;

  /**
   * Creates a new LinearVelocity3d with WPILib velocity measures.
   *
   * @param vx x velocity component
   * @param vy y velocity component
   * @param vz z velocity component
   */
  public LinearVelocity3d(LinearVelocity vx, LinearVelocity vy, LinearVelocity vz) {
    this.vx = Objects.requireNonNull(vx, "vx");
    this.vy = Objects.requireNonNull(vy, "vy");
    this.vz = Objects.requireNonNull(vz, "vz");
  }

  /**
   * Creates a new LinearVelocity3d with the given components.
   *
   * @param vxMetersPerSecond x velocity component in meters per second
   * @param vyMetersPerSecond y velocity component in meters per second
   * @param vzMetersPerSecond z velocity component in meters per second
   */
  public LinearVelocity3d(
      double vxMetersPerSecond, double vyMetersPerSecond, double vzMetersPerSecond) {
    this(
        MetersPerSecond.of(vxMetersPerSecond),
        MetersPerSecond.of(vyMetersPerSecond),
        MetersPerSecond.of(vzMetersPerSecond));
  }

  /**
   * Gets the x velocity component.
   *
   * @return x velocity
   */
  public LinearVelocity getVx() {
    return vx;
  }

  /**
   * Gets the x velocity component.
   *
   * @return x velocity
   */
  public LinearVelocity getX() {
    return vx;
  }

  /**
   * Gets the y velocity component.
   *
   * @return y velocity
   */
  public LinearVelocity getVy() {
    return vy;
  }

  /**
   * Gets the y velocity component.
   *
   * @return y velocity
   */
  public LinearVelocity getY() {
    return vy;
  }

  /**
   * Gets the z velocity component.
   *
   * @return z velocity
   */
  public LinearVelocity getVz() {
    return vz;
  }

  /**
   * Gets the z velocity component.
   *
   * @return z velocity
   */
  public LinearVelocity getZ() {
    return vz;
  }

  /**
   * Gets the x velocity component.
   *
   * @return x velocity in meters per second
   */
  public double getVxMetersPerSecond() {
    return vx.in(MetersPerSecond);
  }

  /**
   * Gets the y velocity component.
   *
   * @return y velocity in meters per second
   */
  public double getVyMetersPerSecond() {
    return vy.in(MetersPerSecond);
  }

  /**
   * Gets the z velocity component.
   *
   * @return z velocity in meters per second
   */
  public double getVzMetersPerSecond() {
    return vz.in(MetersPerSecond);
  }

  /**
   * Gets the magnitude (speed) of this velocity vector.
   *
   * @return speed in meters per second
   */
  public double getMagnitude() {
    double vxMps = getVxMetersPerSecond();
    double vyMps = getVyMetersPerSecond();
    double vzMps = getVzMetersPerSecond();
    return Math.sqrt(vxMps * vxMps + vyMps * vyMps + vzMps * vzMps);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LinearVelocity3d)) {
      return false;
    }
    LinearVelocity3d other = (LinearVelocity3d) obj;
    return vx.equals(other.vx) && vy.equals(other.vy) && vz.equals(other.vz);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vx, vy, vz);
  }

  @Override
  public String toString() {
    return String.format(
        "LinearVelocity3d(%.3f m/s, %.3f m/s, %.3f m/s)",
        getVxMetersPerSecond(), getVyMetersPerSecond(), getVzMetersPerSecond());
  }
}
