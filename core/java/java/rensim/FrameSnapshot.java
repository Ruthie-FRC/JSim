package rensim;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable snapshot of physics world state at a single timestep.
 *
 * <p>Used for serialization, visualization, and deterministic replay.
 */
public final class FrameSnapshot {
  /**
   * Snapshot of a single body's state.
   *
   * @param bodyId unique body identifier
   * @param massKg body mass in kilograms
   * @param positionMeters world-space position in meters
   * @param linearVelocityMps linear velocity in meters per second
   */
  public record BodySnapshot(int bodyId, double massKg, Vec3 positionMeters, Vec3 linearVelocityMps) {
    public BodySnapshot {
      Objects.requireNonNull(positionMeters, "positionMeters cannot be null");
      Objects.requireNonNull(linearVelocityMps, "linearVelocityMps cannot be null");
    }
  }

  private final long stepCount;
  private final double accumulatedTimeSeconds;
  private final List<BodySnapshot> bodySnapshots;

  /**
   * Creates an immutable frame snapshot.
   *
   * @param stepCount simulation step number
   * @param accumulatedTimeSeconds total accumulated simulation time
   * @param bodySnapshots per-body state at this timestep
   */
  public FrameSnapshot(long stepCount, double accumulatedTimeSeconds, List<BodySnapshot> bodySnapshots) {
    this.stepCount = stepCount;
    this.accumulatedTimeSeconds = accumulatedTimeSeconds;
    this.bodySnapshots = List.copyOf(Objects.requireNonNull(bodySnapshots, "bodySnapshots cannot be null"));
  }

  /**
   * Returns the simulation step number at which this frame was captured.
   *
   * @return step count
   */
  public long stepCount() {
    return stepCount;
  }

  /**
   * Returns the total accumulated simulation time at this frame.
   *
   * @return accumulated time in seconds
   */
  public double accumulatedTimeSeconds() {
    return accumulatedTimeSeconds;
  }

  /**
   * Returns the immutable list of body snapshots.
   *
   * @return body state list
   */
  public List<BodySnapshot> bodies() {
    return bodySnapshots;
  }

  /**
   * Finds a body snapshot by ID.
   *
   * @param bodyId the body identifier
   * @return snapshot if found, else null
   */
  public BodySnapshot findBody(int bodyId) {
    for (BodySnapshot body : bodySnapshots) {
      if (body.bodyId() == bodyId) {
        return body;
      }
    }
    return null;
  }
}
