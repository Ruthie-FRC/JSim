package rensim.simulation.sensors;

import java.util.Random;
import rensim.simulation.Pose2;

/**
 * Pose measurement simulator with latency and gaussian noise.
 */
public final class VisionSimulation {
  private final Random random;
  private double positionStdDevMeters;
  private double yawStdDevRad;
  private double latencySeconds;

  public VisionSimulation(long seed, double positionStdDevMeters, double yawStdDevRad,
      double latencySeconds) {
    this.random = new Random(seed);
    this.positionStdDevMeters = Math.max(0.0, positionStdDevMeters);
    this.yawStdDevRad = Math.max(0.0, yawStdDevRad);
    this.latencySeconds = Math.max(0.0, latencySeconds);
  }

  public record VisionSample(Pose2 measuredPose, double latencySeconds) {}

  public VisionSample sample(Pose2 truePose) {
    double x = truePose.xMeters() + random.nextGaussian() * positionStdDevMeters;
    double y = truePose.yMeters() + random.nextGaussian() * positionStdDevMeters;
    double yaw = truePose.yawRad() + random.nextGaussian() * yawStdDevRad;
    return new VisionSample(new Pose2(x, y, yaw), latencySeconds);
  }

  public void setNoise(double positionStdDevMeters, double yawStdDevRad) {
    this.positionStdDevMeters = Math.max(0.0, positionStdDevMeters);
    this.yawStdDevRad = Math.max(0.0, yawStdDevRad);
  }

  public void setLatencySeconds(double latencySeconds) {
    this.latencySeconds = Math.max(0.0, latencySeconds);
  }
}
