package rensim.simulation.motorsims;

/**
 * State of a simulated motor at one instant.
 */
public final class SimMotorState {
  private double mechanismAngularPositionRad;
  private double mechanismAngularVelocityRadPerSec;

  public SimMotorState(double initialPositionRad, double initialVelocityRadPerSec) {
    this.mechanismAngularPositionRad = initialPositionRad;
    this.mechanismAngularVelocityRadPerSec = initialVelocityRadPerSec;
  }

  public void step(double electricTorqueNm, double frictionTorqueNm, double loadMoiKgM2,
      double dtSeconds, double velocityDeadbandMps) {
    if (!(loadMoiKgM2 > 0.0) || !(dtSeconds > 0.0)) {
      throw new IllegalArgumentException("loadMoiKgM2 and dtSeconds must be > 0");
    }

    mechanismAngularVelocityRadPerSec += electricTorqueNm / loadMoiKgM2 * dtSeconds;

    double frictionDelta = Math.copySign(frictionTorqueNm, -mechanismAngularVelocityRadPerSec)
        / loadMoiKgM2 * dtSeconds;
    double candidate = mechanismAngularVelocityRadPerSec + frictionDelta;
    if (candidate * mechanismAngularVelocityRadPerSec <= 0.0
        || Math.abs(candidate) <= velocityDeadbandMps) {
      mechanismAngularVelocityRadPerSec = 0.0;
    } else {
      mechanismAngularVelocityRadPerSec = candidate;
    }

    mechanismAngularPositionRad += mechanismAngularVelocityRadPerSec * dtSeconds;
  }

  public double mechanismAngularPositionRad() {
    return mechanismAngularPositionRad;
  }

  public double mechanismAngularVelocityRadPerSec() {
    return mechanismAngularVelocityRadPerSec;
  }
}