package rensim.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import rensim.Vec3;

/**
 * Generic scoring goal that consumes game pieces entering a configured volume.
 */
public class Goal implements SimulatedArena.Simulatable {
  private final SimulatedArena arena;
  private final String acceptedType;
  private final Vec3 min;
  private final Vec3 max;
  private final Predicate<GamePieceOnFieldSimulation> velocityValidator;
  private int scoredCount;

  /**
   * Creates a goal with axis-aligned bounds and piece type filter.
   */
  public Goal(SimulatedArena arena, String acceptedType, Vec3 min, Vec3 max,
      Predicate<GamePieceOnFieldSimulation> velocityValidator) {
    this.arena = Objects.requireNonNull(arena);
    this.acceptedType = Objects.requireNonNull(acceptedType);
    this.min = Objects.requireNonNull(min);
    this.max = Objects.requireNonNull(max);
    this.velocityValidator = Objects.requireNonNull(velocityValidator);
  }

  /**
   * Convenience goal accepting all entry velocities.
   */
  public Goal(SimulatedArena arena, String acceptedType, Vec3 min, Vec3 max) {
    this(arena, acceptedType, min, max, piece -> true);
  }

  @Override
  public void simulationSubTick(int subTickNum) {
    List<GamePieceOnFieldSimulation> toScore = new ArrayList<>();
    for (GamePieceOnFieldSimulation piece : arena.gamePiecesOnField()) {
      if (!piece.type().equals(acceptedType)) {
        continue;
      }
      if (!velocityValidator.test(piece)) {
        continue;
      }
      Vec3 p = piece.pose().positionMeters();
      if (contains(p)) {
        toScore.add(piece);
      }
    }

    for (GamePieceOnFieldSimulation piece : toScore) {
      arena.removePiece(piece);
      piece.triggerHitTargetCallback();
      scoredCount++;
    }
  }

  /**
   * Returns total pieces scored in this goal.
   */
  public int scoredCount() {
    return scoredCount;
  }

  private boolean contains(Vec3 p) {
    return p.x() >= min.x() && p.x() <= max.x()
        && p.y() >= min.y() && p.y() <= max.y()
        && p.z() >= min.z() && p.z() <= max.z();
  }
}