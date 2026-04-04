package rensim.simulation.motorsims;

/**
 * Compact motor configuration model used by simulation motor loops.
 */
public final class SimMotorConfigs {
  private final double torqueConstantNmPerAmp;
  private final double resistanceOhm;
  private final double backEmfConstantVoltPerRadPerSec;
  private final double gearRatio;
  private final double frictionTorqueNm;
  private final double currentLimitAmps;
  private final double loadMoiKgM2;

  public SimMotorConfigs(double torqueConstantNmPerAmp, double resistanceOhm,
      double backEmfConstantVoltPerRadPerSec, double gearRatio, double frictionTorqueNm,
      double currentLimitAmps, double loadMoiKgM2) {
    if (!(torqueConstantNmPerAmp > 0.0) || !(resistanceOhm > 0.0)
        || !(backEmfConstantVoltPerRadPerSec > 0.0) || !(gearRatio > 0.0)
        || frictionTorqueNm < 0.0 || !(currentLimitAmps > 0.0) || !(loadMoiKgM2 > 0.0)) {
      throw new IllegalArgumentException("Invalid motor configuration values");
    }
    this.torqueConstantNmPerAmp = torqueConstantNmPerAmp;
    this.resistanceOhm = resistanceOhm;
    this.backEmfConstantVoltPerRadPerSec = backEmfConstantVoltPerRadPerSec;
    this.gearRatio = gearRatio;
    this.frictionTorqueNm = frictionTorqueNm;
    this.currentLimitAmps = currentLimitAmps;
    this.loadMoiKgM2 = loadMoiKgM2;
  }

  public static SimMotorConfigs falcon500Like(double gearRatio) {
    return new SimMotorConfigs(0.0182, 0.0467, 0.0197, gearRatio, 0.18, 60.0, 0.02);
  }

  public double calculateCurrent(double mechanismAngularVelocityRadPerSec, double appliedVoltage) {
    double motorSpeed = mechanismAngularVelocityRadPerSec * gearRatio;
    double emf = backEmfConstantVoltPerRadPerSec * motorSpeed;
    double rawCurrent = (appliedVoltage - emf) / resistanceOhm;
    if (rawCurrent > currentLimitAmps) {
      return currentLimitAmps;
    }
    if (rawCurrent < -currentLimitAmps) {
      return -currentLimitAmps;
    }
    return rawCurrent;
  }

  public double calculateTorque(double statorCurrentAmps) {
    double motorTorque = torqueConstantNmPerAmp * statorCurrentAmps;
    double geared = motorTorque * gearRatio;
    double signedFriction = Math.copySign(frictionTorqueNm, geared == 0.0 ? 1.0 : geared);
    double frictionApplied = Math.abs(geared) > frictionTorqueNm ? geared - signedFriction : 0.0;
    return frictionApplied;
  }

  public double loadMoiKgM2() {
    return loadMoiKgM2;
  }
}