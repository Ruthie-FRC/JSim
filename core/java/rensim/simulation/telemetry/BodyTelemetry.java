package rensim.simulation.telemetry;

import java.util.Objects;

/**
 * Per-body telemetry payload exported by simulation runtime integrations.
 *
 * @param name body identifier
 * @param xMeters x position in meters
 * @param yMeters y position in meters
 * @param positionFrameTag position frame tag ('w' world or 'b' body)
 * @param vxMps x velocity in m/s
 * @param vyMps y velocity in m/s
 * @param speedMps planar speed in m/s
 * @param velocityFrameTag velocity frame tag ('w' world or 'b' body)
 */
public record BodyTelemetry(
    String name,
    double xMeters,
    double yMeters,
    FrameTag positionFrameTag,
    double vxMps,
    double vyMps,
    double speedMps,
    FrameTag velocityFrameTag) {
  public BodyTelemetry {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(positionFrameTag, "positionFrameTag");
    Objects.requireNonNull(velocityFrameTag, "velocityFrameTag");
    if (name.isBlank()) {
      throw new IllegalArgumentException("name must be non-empty");
    }
    if (!Double.isFinite(xMeters)
        || !Double.isFinite(yMeters)
        || !Double.isFinite(vxMps)
        || !Double.isFinite(vyMps)
        || !Double.isFinite(speedMps)) {
      throw new IllegalArgumentException("telemetry values must be finite SI values");
    }
    if (speedMps < 0.0) {
      throw new IllegalArgumentException("speedMps must be >= 0");
    }
  }
}
