// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jsim.Ball;
import jsim.PhysicsWorld;
import jsim.api.RobotID;
import jsim.api.StateManager;

/**
 * Auto-publishes field telemetry for balls and robot poses.
 *
 * <p>Publishes structured pose arrays to AdvantageScope-friendly topics:
 * <ul>
 *   <li>{@code /jsim/world/ballPoses}</li>
 *   <li>{@code /jsim/world/robotPoses}</li>
 * </ul>
 *
 * <p>The publisher registers itself with the world step loop, so calling
 * {@link PhysicsWorld#step()} refreshes the topics automatically.
 */
public final class FieldTelemetryPublisher implements AutoCloseable {
  private final PhysicsWorld world;
  private final StateManager stateManager;

  private final StructArrayPublisher<Pose3d> ballPosesPublisher;
  private final StructArrayPublisher<Pose2d> robotPosesPublisher;

  /**
   * Creates a publisher using the default NT instance and {@code /jsim/world} topics.
   *
   * @param world physics world to observe
   * @param stateManager robot state source
   */
  public FieldTelemetryPublisher(PhysicsWorld world, StateManager stateManager) {
    this(world, stateManager, NetworkTableInstance.getDefault(), "/jsim/world");
  }

  /**
   * Creates a publisher bound to the provided NT instance and base topic.
   *
   * @param world physics world to observe
   * @param stateManager robot state source
   * @param ntInstance NT instance to publish through
   * @param baseTopic base topic path, for example {@code /jsim/world}
   */
  public FieldTelemetryPublisher(
      PhysicsWorld world,
      StateManager stateManager,
      NetworkTableInstance ntInstance,
      String baseTopic) {
    this.world = world;
    this.stateManager = stateManager;

    NetworkTable table = ntInstance.getTable(baseTopic);
    this.ballPosesPublisher = table.getStructArrayTopic("ballPoses", Pose3d.struct).publish();
    this.robotPosesPublisher = table.getStructArrayTopic("robotPoses", Pose2d.struct).publish();

    this.world.addStepListener(this::publishFrame);
  }

  /**
   * Publishes the current ball and robot poses once.
   */
  public void publishFrame() {
    List<Pose3d> ballPoses = new ArrayList<>();
    for (Ball ball : world.balls()) {
      ballPoses.add(ball.position());
    }
    ballPosesPublisher.set(ballPoses.toArray(new Pose3d[0]));

    Map<RobotID, Pose2d> robotPoses = stateManager.getRobotPoses();
    List<Pose2d> orderedRobotPoses = new ArrayList<>(robotPoses.size());
    for (RobotID robotId : RobotID.values()) {
      Pose2d pose = robotPoses.get(robotId);
      if (pose != null) {
        orderedRobotPoses.add(pose);
      }
    }
    robotPosesPublisher.set(orderedRobotPoses.toArray(new Pose2d[0]));
  }

  @Override
  public void close() {
    ballPosesPublisher.close();
    robotPosesPublisher.close();
  }
}