// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

/**
 * Handle for a revolute joint managed by {@link PhysicsWorld}.
 */
public final class RevoluteJoint {
  private final PhysicsWorld world;
  private final int jointId;

  RevoluteJoint(PhysicsWorld world, int jointId) {
    this.world = world;
    this.jointId = jointId;
  }

  /**
   * Returns the native joint id.
   *
   * @return the native joint id
   */
  public int jointId() {
    return jointId;
  }

  /**
   * Sets angular limits for the joint.
   *
   * @param minAngleRad minimum angle in radians
   * @param maxAngleRad maximum angle in radians
   */
  public void setLimits(double minAngleRad, double maxAngleRad) {
    world.setJointLimits(jointId, minAngleRad, maxAngleRad);
  }

  /**
   * Configures the joint motor target.
   *
   * @param targetVelocityRadps target angular velocity in rad/s
   * @param maxTorqueNm maximum torque in N·m
   */
  public void setMotorTarget(double targetVelocityRadps, double maxTorqueNm) {
    world.setJointMotorTarget(jointId, targetVelocityRadps, maxTorqueNm);
  }

  /**
   * Returns the current relative angle across the joint.
   *
   * @return joint angle in radians
   */
  public double angleRadians() {
    return world.getJointAngleRadians(jointId);
  }

  /**
   * Returns the current relative angular velocity across the joint.
   *
   * @return angular velocity in rad/s
   */
  public double velocityRadiansPerSecond() {
    return world.getJointVelocityRadiansPerSecond(jointId);
  }
}