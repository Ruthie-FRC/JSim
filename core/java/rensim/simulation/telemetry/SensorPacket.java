package rensim.simulation.telemetry;

import java.util.List;

/**
 * Frame-level telemetry packet exported by runtime simulation integrations.
 *
 * @param tick simulation tick index
 * @param timeSeconds simulation time in seconds
 * @param contactCount number of contacts resolved on this tick
 * @param bodies per-body telemetry snapshots
 */
public record SensorPacket(
    int tick,
    double timeSeconds,
    int contactCount,
    List<BodyTelemetry> bodies) {
  public SensorPacket {
    if (tick < 0) {
      throw new IllegalArgumentException("tick must be >= 0");
    }
    if (!Double.isFinite(timeSeconds) || timeSeconds < 0.0) {
      throw new IllegalArgumentException("timeSeconds must be finite and >= 0");
    }
    if (contactCount < 0) {
      throw new IllegalArgumentException("contactCount must be >= 0");
    }
    bodies = List.copyOf(bodies);
  }

  public void requireWorldFrames() {
    for (BodyTelemetry body : bodies) {
      if (body.positionFrameTag() != FrameTag.WORLD) {
        throw new IllegalArgumentException("position frame must be world for NT flattening: " + body.name());
      }
      if (body.velocityFrameTag() != FrameTag.WORLD) {
        throw new IllegalArgumentException("velocity frame must be world for NT flattening: " + body.name());
      }
    }
  }
}
