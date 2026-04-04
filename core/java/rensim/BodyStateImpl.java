package rensim;

import java.util.Objects;

/**
 * Implementation of BodyStateView for immutable body state snapshots.
 *
 * <p>Safe for concurrent access and long-term storage.
 */
final class BodyStateImpl implements BodyStateView {
  private final int id;
  private final double massKg;
  private final Vec3 position;
  private final Vec3 linearVelocity;
  private final boolean gravityEnabled;

  BodyStateImpl(int id, double massKg, Vec3 position, Vec3 linearVelocity, boolean gravityEnabled) {
    this.id = id;
    this.massKg = massKg;
    this.position = Objects.requireNonNull(position);
    this.linearVelocity = Objects.requireNonNull(linearVelocity);
    this.gravityEnabled = gravityEnabled;
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public double massKg() {
    return massKg;
  }

  @Override
  public Vec3 position() {
    return position;
  }

  @Override
  public Vec3 linearVelocity() {
    return linearVelocity;
  }

  @Override
  public boolean gravityEnabled() {
    return gravityEnabled;
  }
}