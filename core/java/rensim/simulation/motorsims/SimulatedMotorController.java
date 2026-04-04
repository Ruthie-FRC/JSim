package rensim.simulation.motorsims;

/**
 * Abstraction for simulated motor controllers.
 */
public interface SimulatedMotorController {
  /**
   * Computes control signal voltage.
   */
  double updateControlSignal(double mechanismPositionRad, double mechanismVelocityRadPerSec,
      double encoderPositionRad, double encoderVelocityRadPerSec);

  /**
   * Generic open-loop voltage controller.
   */
  final class GenericMotorController implements SimulatedMotorController {
    private double requestedVoltage;

    public GenericMotorController requestVoltage(double voltage) {
      this.requestedVoltage = voltage;
      return this;
    }

    @Override
    public double updateControlSignal(double mechanismPositionRad, double mechanismVelocityRadPerSec,
        double encoderPositionRad, double encoderVelocityRadPerSec) {
      return requestedVoltage;
    }
  }
}