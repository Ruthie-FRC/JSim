// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.nt;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Quaternion;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.DoubleArrayPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import jsim.jni.JSimJNI;

/**
 * Publishes JSim body state to NetworkTables in AdvantageScope-friendly formats.
 *
 * <p>Publishes both:
 * <ul>
 *   <li>Structured Pose3d array: {@code /jsim/world/bodyPoses}</li>
 *   <li>Flat pose buffer [x,y,z,qw,qx,qy,qz...]: {@code /jsim/world/bodyPose7Flat}</li>
 *   <li>Flat velocity buffer [vx,vy,vz,wx,wy,wz...]: {@code /jsim/world/bodyVelocity6Flat}</li>
 * </ul>
 */
public class WorldPosePublisher implements AutoCloseable {
  private final long worldHandle;
  private final int maxBodies;

  private final StructArrayPublisher<Pose3d> bodyPosesPublisher;
  private final DoubleArrayPublisher bodyPoseFlatPublisher;
  private final DoubleArrayPublisher bodyVelocityFlatPublisher;

  private final double[] pose7Buffer;
  private final double[] velocity6Buffer;

  /**
   * Creates a new world publisher under {@code /jsim/world}.
   *
   * @param worldHandle native world handle
   * @param maxBodies maximum number of bodies to export per frame
   */
  public WorldPosePublisher(long worldHandle, int maxBodies) {
    this(worldHandle, maxBodies, NetworkTableInstance.getDefault(), "/jsim/world");
  }

  /**
   * Creates a new world publisher.
   *
   * @param worldHandle native world handle
   * @param maxBodies maximum number of bodies to export per frame
   * @param ntInstance network table instance
   * @param baseTopic base topic path (e.g. {@code /jsim/world})
   */
  public WorldPosePublisher(
      long worldHandle,
      int maxBodies,
      NetworkTableInstance ntInstance,
      String baseTopic) {
    this.worldHandle = worldHandle;
    this.maxBodies = Math.max(1, maxBodies);
    this.pose7Buffer = new double[this.maxBodies * 7];
    this.velocity6Buffer = new double[this.maxBodies * 6];

    NetworkTable table = ntInstance.getTable(baseTopic);
    this.bodyPosesPublisher = table.getStructArrayTopic("bodyPoses", Pose3d.struct).publish();
    this.bodyPoseFlatPublisher = table.getDoubleArrayTopic("bodyPose7Flat").publish();
    this.bodyVelocityFlatPublisher = table.getDoubleArrayTopic("bodyVelocity6Flat").publish();
  }

  /**
   * Pulls world state from JNI and publishes one frame.
   *
   * @return number of body entries published, or negative on native error
   */
  public int publishFrame() {
    int poseCount = JSimJNI.getBodyPose7Array(worldHandle, pose7Buffer);
    if (poseCount < 0) {
      return poseCount;
    }

    int velocityCount = JSimJNI.getBodyVelocity6Array(worldHandle, velocity6Buffer);
    if (velocityCount < 0) {
      return velocityCount;
    }

    int count = Math.min(poseCount, velocityCount);
    Pose3d[] poses = new Pose3d[count];
    for (int i = 0; i < count; i++) {
      int base = i * 7;
      Translation3d translation =
          new Translation3d(pose7Buffer[base], pose7Buffer[base + 1], pose7Buffer[base + 2]);
      Rotation3d rotation =
          new Rotation3d(
              new Quaternion(
                  pose7Buffer[base + 3],
                  pose7Buffer[base + 4],
                  pose7Buffer[base + 5],
                  pose7Buffer[base + 6]));
      poses[i] = new Pose3d(translation, rotation);
    }

    bodyPosesPublisher.set(poses);

    double[] poseFlat = new double[count * 7];
    System.arraycopy(pose7Buffer, 0, poseFlat, 0, poseFlat.length);
    bodyPoseFlatPublisher.set(poseFlat);

    double[] velocityFlat = new double[count * 6];
    System.arraycopy(velocity6Buffer, 0, velocityFlat, 0, velocityFlat.length);
    bodyVelocityFlatPublisher.set(velocityFlat);

    return count;
  }

  @Override
  public void close() {
    bodyPosesPublisher.close();
    bodyPoseFlatPublisher.close();
    bodyVelocityFlatPublisher.close();
  }
}
