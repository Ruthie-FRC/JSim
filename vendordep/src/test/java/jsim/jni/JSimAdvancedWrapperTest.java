// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.jni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jsim.PhysicsBody;
import jsim.PhysicsWorld;
import org.junit.jupiter.api.Test;

class JSimAdvancedWrapperTest {
  @Test
  void advancedWorldAndMaterialWrappersWork() {
    try (PhysicsWorld world = new PhysicsWorld(0.01, true)) {
      world.setGravity(0.0, 0.0, -9.81);
      world.setAerodynamics(true, 1.2, 0.05, 0.001, 0.47, 0.02);
      world.setMaterialInteraction(1, 2, 0.6, 0.4, true);

      PhysicsBody bodyA = world.createBody(5.0);
      bodyA.setPosition(0.0, 0.0, 1.0);
      bodyA.setLinearVelocity(0.2, 0.0, 0.0);
      bodyA.setMaterial(0.5, 0.4, 0.6, 0.1);
      bodyA.setMaterialId(1);

      PhysicsBody bodyB = world.createBody(7.0);
      bodyB.setPosition(1.0, 0.0, 1.0);
      bodyB.setMaterial(0.7, 0.3, 0.5, 0.2);
      bodyB.setMaterialId(2);

      world.step(5);

      double[] posA = bodyA.positionArray();
      assertEquals(3, posA.length);
      assertTrue(posA[2] < 1.0);
    }
  }

  @Test
  void bodyBatchExportWrappersWork() {
    try (PhysicsWorld world = new PhysicsWorld(0.01, true)) {
      world.createBody(1.0).setPosition(0.0, 0.0, 1.0);
      world.createBody(2.0).setPosition(1.0, 0.0, 2.0);

      world.step(1);

      double[] pose7 = new double[28];
      double[] vel6 = new double[24];
      double[] state13 = new double[52];

      int poseCount = world.getBodyPose7Array(pose7);
      int velCount = world.getBodyVelocity6Array(vel6);
      int stateCount = world.getBodyState13Array(state13);

      assertTrue(poseCount >= 2);
      assertTrue(velCount >= 2);
      assertTrue(stateCount >= 2);
    }
  }
}
