package rensim.simulation.motorsims;

import java.util.Objects;

/**
 * Simulated motor loop with configurable motor model and controller.
 */
public final class MapleMotorSim {
  private final SimMotorConfigs configs;
  private final SimMotorState state;
  private SimulatedMotorController controller;
  private double appliedVoltage;
  private double statorCurrentAmps;

  public MapleMotorSim(SimMotorConfigs configs) {
    this(configs, new SimMotorState(0.0, 0.0));
  }

  public MapleMotorSim(SimMotorConfigs configs, SimMotorState initialState) {
    this.configs = Objects.requireNonNull(configs);
    this.state = Objects.requireNonNull(initialState);
    this.controller = new SimulatedMotorController.GenericMotorController();
  }

  public <T extends SimulatedMotorController> T useController(T controller) {
    this.controller = Objects.requireNonNull(controller);
    return controller;
  }

  public SimulatedMotorController.GenericMotorController useSimpleDCMotorController() {
    SimulatedMotorController.GenericMotorController generic =
        new SimulatedMotorController.GenericMotorController();
    this.controller = generic;
    return generic;
  }

  public void update(double dtSeconds, double velocityDeadbandRadPerSec) {
    appliedVoltage = clampVoltage(controller.updateControlSignal(
        state.mechanismAngularPositionRad(),
        state.mechanismAngularVelocityRadPerSec(),
        state.mechanismAngularPositionRad(),
        state.mechanismAngularVelocityRadPerSec()));
    statorCurrentAmps = configs.calculateCurrent(state.mechanismAngularVelocityRadPerSec(), appliedVoltage);
    double electricTorque = configs.calculateTorque(statorCurrentAmps);
    double frictionTorque = Math.abs(electricTorque) > 0.0 ? Math.abs(electricTorque) * 0.02 : 0.0;
    state.step(electricTorque, frictionTorque, configs.loadMoiKgM2(), dtSeconds,
        velocityDeadbandRadPerSec);
  }

  public double getAppliedVoltage() {
    return appliedVoltage;
  }

  public double getStatorCurrentAmps() {
    return statorCurrentAmps;
  }

  public double getEncoderPositionRad() {
    return state.mechanismAngularPositionRad();
  }

  public double getEncoderVelocityRadPerSec() {
    return state.mechanismAngularVelocityRadPerSec();
  }

  private static double clampVoltage(double voltage) {
    if (voltage > 12.0) {
      return 12.0;
    }
    if (voltage < -12.0) {
      return -12.0;
    }
    return voltage;
  }
}