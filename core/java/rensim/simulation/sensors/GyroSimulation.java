package rensim.simulation.sensors;

import java.util.Random;

/**
 * Lightweight simulated gyro with configurable drift and gaussian measurement noise.
 */
public final class GyroSimulation {
  private final Random random;
  private double yawRad;
  private double angularVelocityRadPerSec;
  private double driftPerSecondRad;
  private double noiseStdDevRad;

  public GyroSimulation(long seed, double driftPerSecondRad, double noiseStdDevRad) {
    this.random = new Random(seed);
    this.driftPerSecondRad = driftPerSecondRad;
    this.noiseStdDevRad = Math.max(0.0, noiseStdDevRad);
  }

  public void update(double trueAngularVelocityRadPerSec, double dtSeconds) {
    angularVelocityRadPerSec = trueAngularVelocityRadPerSec;
    yawRad += (trueAngularVelocityRadPerSec + driftPerSecondRad) * dtSeconds;
  }

  public void applyCollisionImpulse(double impulseRadPerSec) {
    yawRad += impulseRadPerSec * 0.02;
  }

  public double getGyroReadingRad() {
    return yawRad + random.nextGaussian() * noiseStdDevRad;
  }

  public double getMeasuredAngularVelocityRadPerSec() {
    return angularVelocityRadPerSec + random.nextGaussian() * noiseStdDevRad;
  }

  public void setDriftPerSecondRad(double driftPerSecondRad) {
    this.driftPerSecondRad = driftPerSecondRad;
  }

  public void reset(double yawRad) {
    this.yawRad = yawRad;
    this.angularVelocityRadPerSec = 0.0;
  }
}
