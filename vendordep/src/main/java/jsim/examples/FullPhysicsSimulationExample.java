// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.examples;

import jsim.Ball;
import jsim.PhysicsBody;
import jsim.PhysicsWorld;
import jsim.Vec3;

/**
 * End-to-end example for creating a world, configuring materials/aerodynamics,
 * spawning rigid bodies and balls, and stepping the simulation.
 */
public final class FullPhysicsSimulationExample {
  private FullPhysicsSimulationExample() {}

  public static void main(String[] args) {
    try (PhysicsWorld world = new PhysicsWorld(0.01, true)) {
      world.setGravity(0.0, 0.0, -9.81);
      world.setAerodynamics(true, 1.2, 0.04, 0.001, 0.47, 0.02);
      world.setMaterialInteraction(1, 2, 0.55, 0.35, true);

      PhysicsBody floor = world.createBody(1000.0);
      floor.setCollisionBox(8.0, 8.0, 0.05);
      floor.setPosition(0.0, 0.0, -0.025);
      floor.setMaterial(0.25, 0.8, 0.9, 0.2);
      floor.setMaterialId(1);

      PhysicsBody bumper = world.createBody(15.0);
      bumper.setCollisionBox(0.6, 0.6, 0.2);
      bumper.setPosition(1.0, 0.0, 0.1);
      bumper.setMaterial(0.5, 0.5, 0.6, 0.15);
      bumper.setMaterialId(2);

      Ball ball = world.createBall();
      ball.shoot(new Vec3(0.0, 0.0, 0.3), new Vec3(3.5, 0.0, 1.8));

      for (int i = 0; i < 200; i++) {
        world.step();
      }

      Vec3 finalBallPos = ball.position();
      Vec3 finalBallVel = ball.linearVelocity();
      System.out.printf(
          "Ball final pos: (%.3f, %.3f, %.3f)%n", finalBallPos.x(), finalBallPos.y(), finalBallPos.z());
      System.out.printf(
          "Ball final vel: (%.3f, %.3f, %.3f)%n", finalBallVel.x(), finalBallVel.y(), finalBallVel.z());
    }
  }
}
