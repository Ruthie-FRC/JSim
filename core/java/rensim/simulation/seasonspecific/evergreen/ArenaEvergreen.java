package rensim.simulation.seasonspecific.evergreen;

import java.util.List;
import java.util.Objects;
import rensim.Vec3;
import rensim.simulation.GamePieceOnFieldSimulation;
import rensim.simulation.Pose2;
import rensim.simulation.SimulatedArena;
import rensim.simulation.SimulationOptions;

/**
 * Evergreen: baseline field with optional walls and utility game pieces.
 */
public final class ArenaEvergreen extends SimulatedArena {
  /** Evergreen field map. */
  public static final class EvergreenFieldObstacleMap extends FieldMap {
    public EvergreenFieldObstacleMap() {}

    /** Adds perimeter walls for collision testing. */
    public EvergreenFieldObstacleMap withWalls(double widthMeters, double heightMeters) {
      addBorderLine(0.0, 0.0, widthMeters, 0.0);
      addBorderLine(widthMeters, 0.0, widthMeters, heightMeters);
      addBorderLine(widthMeters, heightMeters, 0.0, heightMeters);
      addBorderLine(0.0, heightMeters, 0.0, 0.0);
      return this;
    }
  }

  private static final GamePieceOnFieldSimulation.GamePieceInfo TEST_PIECE =
      new GamePieceOnFieldSimulation.GamePieceInfo("EvergreenPiece", 0.24, 0.18, 0.03, 0.03, 0.4);

  public ArenaEvergreen(boolean withWalls) {
    this(SimulationOptions.defaults(), withWalls);
  }

  public ArenaEvergreen(SimulationOptions options, boolean withWalls) {
    super(options, withWalls
        ? new EvergreenFieldObstacleMap().withWalls(options.boundaries().widthMeters(),
            options.boundaries().heightMeters())
        : new EvergreenFieldObstacleMap());
  }

  @Override
  public void placeGamePiecesOnField() {
    addGamePiece(new GamePieceOnFieldSimulation(this, TEST_PIECE, new Pose2(4.0, 3.0, 0.0), Vec3.ZERO));
    addGamePiece(new GamePieceOnFieldSimulation(this, TEST_PIECE, new Pose2(8.0, 4.0, 0.0), Vec3.ZERO));
  }

  /**
   * Repositions all evergreen pieces to a line for quick reset.
   */
  public void resetPiecesLine(double startX, double y, double spacingMeters) {
    Objects.requireNonNull(gamePiecesOnField());
    int i = 0;
    for (GamePieceOnFieldSimulation piece : List.copyOf(gamePiecesOnField())) {
      if (piece.type().equals(TEST_PIECE.type())) {
        setGamePiecePose(piece, new Pose2(startX + spacingMeters * i, y, 0.0));
        setGamePieceVelocity(piece, Vec3.ZERO);
        i++;
      }
    }
  }
}