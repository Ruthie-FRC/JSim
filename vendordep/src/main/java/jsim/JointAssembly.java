// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

/**
 * Logical container for joint-driven articulated mechanisms.
 */
public final class JointAssembly {
  private final PhysicsWorld world;
  private final int assemblyIndex;

  JointAssembly(PhysicsWorld world, int assemblyIndex) {
    this.world = world;
    this.assemblyIndex = assemblyIndex;
  }

  /**
   * Returns the native assembly index.
   *
   * @return the native assembly index
   */
  public int assemblyIndex() {
    return assemblyIndex;
  }

  /**
   * Creates a revolute joint in this assembly.
   *
   * @param bodyAIndex first body index
   * @param bodyBIndex second body index
   * @param axisLocal hinge axis in body A local space
   * @return the new joint handle
   */
  public RevoluteJoint createRevoluteJoint(int bodyAIndex, int bodyBIndex, Vec3 axisLocal) {
    int jointId = world.addRevoluteJoint(assemblyIndex, bodyAIndex, bodyBIndex, axisLocal);
    return new RevoluteJoint(world, jointId);
  }
}