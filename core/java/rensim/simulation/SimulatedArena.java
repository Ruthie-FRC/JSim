package rensim.simulation;

import frcsim_physics.RigidBody;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import rensim.PhysicsBody;
import rensim.PhysicsWorld;
import rensim.Vec3;
import rensim.simulation.drivesims.SwerveDriveSimulation;

/**
 * Maple-style simulation world orchestrator using RenSim physics primitives.
 */
public abstract class SimulatedArena {
  /**
   * Custom simulation callback run each sub-tick.
   */
  @FunctionalInterface
  public interface Simulatable {
    void simulationSubTick(int subTickNum);
  }

  /**
   * Field map abstraction for boundary and obstacle setup.
   */
  public abstract static class FieldMap {
    private final List<LineSegment> borderLines = new ArrayList<>();
    private final List<RectObstacle> rectangularObstacles = new ArrayList<>();

    protected void addBorderLine(double startX, double startY, double endX, double endY) {
      borderLines.add(new LineSegment(startX, startY, endX, endY));
    }

    protected void addRectangularObstacle(double widthMeters, double heightMeters, Pose2 pose) {
      if (!(widthMeters > 0.0) || !(heightMeters > 0.0)) {
        throw new IllegalArgumentException("Obstacle dimensions must be > 0");
      }
      rectangularObstacles.add(new RectObstacle(widthMeters, heightMeters, pose));
    }

    List<LineSegment> borderLines() {
      return List.copyOf(borderLines);
    }

    List<RectObstacle> rectangularObstacles() {
      return List.copyOf(rectangularObstacles);
    }
  }

  /**
   * 2D line segment used for border storage.
   */
  public record LineSegment(double startX, double startY, double endX, double endY) {}

  /**
   * Axis-aligned rectangle obstacle descriptor.
   */
  public record RectObstacle(double widthMeters, double heightMeters, Pose2 pose) {}

  protected final SimulationOptions options;
  protected final FieldMap fieldMap;
  protected final PhysicsWorld world;

  protected final List<SwerveDriveSimulation> driveTrainSimulations = new ArrayList<>();
  protected final List<Simulatable> customSimulations = new ArrayList<>();
  protected final List<IntakeSimulation> intakeSimulations = new ArrayList<>();
  protected final Set<GamePieceOnFieldSimulation> gamePiecesOnField = new HashSet<>();
  protected final Set<GamePieceProjectile> gamePieceProjectiles = new HashSet<>();

  /**
   * Creates a simulated arena with configurable options and field map.
   */
  protected SimulatedArena(SimulationOptions options, FieldMap fieldMap) {
    this.options = Objects.requireNonNull(options);
    this.fieldMap = Objects.requireNonNull(fieldMap);
    this.world = new PhysicsWorld(options.timing().fixedDtSeconds(), true);
    this.world.setGravity(options.gravityMps2());
    this.world.setSimpleSphereCollisionsEnabled(options.collision().enabled());
  }

  /**
   * Steps full robot-period simulation using configured sub-ticks.
   */
  public synchronized void simulationPeriodic() {
    for (int subTick = 0; subTick < options.timing().subTicksPerRobotPeriod(); subTick++) {
      simulationSubTick(subTick);
    }
  }

  /**
   * Runs one sub-tick update pass.
   */
  protected void simulationSubTick(int subTickNum) {
    for (SwerveDriveSimulation drive : driveTrainSimulations) {
      drive.simulationSubTick(options.timing().fixedDtSeconds());
    }

    for (GamePieceProjectile projectile : List.copyOf(gamePieceProjectiles)) {
      projectile.update(options.timing().fixedDtSeconds(), options);
      if (projectile.hasHitGround()) {
        GamePieceOnFieldSimulation grounded = projectile.addGamePieceAfterTouchGround(this);
        if (grounded != null) {
          addGamePiece(grounded);
        }
      }
      if (projectile.hasHitGround() || projectile.hasHitTarget() || projectile.hasGoneOutOfField()) {
        removePiece(projectile);
      }
    }

    world.step();

    for (IntakeSimulation intake : intakeSimulations) {
      intake.removeObtainedGamePieces(this, defaultIntakePose(), 0.5);
    }

    for (Simulatable custom : customSimulations) {
      custom.simulationSubTick(subTickNum);
    }
  }

  /**
   * Override to define autonomous-period game-piece placements.
   */
  public abstract void placeGamePiecesOnField();

  /**
   * Registers drivetrain simulation.
   */
  public synchronized void addDriveTrainSimulation(SwerveDriveSimulation driveTrainSimulation) {
    driveTrainSimulations.add(Objects.requireNonNull(driveTrainSimulation));
  }

  /**
   * Registers intake simulation.
   */
  public synchronized void addIntakeSimulation(IntakeSimulation intakeSimulation) {
    intakeSimulations.add(Objects.requireNonNull(intakeSimulation));
  }

  /**
   * Registers custom simulation callback.
   */
  public synchronized void addCustomSimulation(Simulatable customSimulation) {
    customSimulations.add(Objects.requireNonNull(customSimulation));
  }

  /**
   * Adds grounded game piece.
   */
  public synchronized void addGamePiece(GamePieceOnFieldSimulation piece) {
    gamePiecesOnField.add(Objects.requireNonNull(piece));
  }

  /**
   * Adds projectile game piece.
   */
  public synchronized void addProjectile(GamePieceProjectile projectile) {
    gamePieceProjectiles.add(Objects.requireNonNull(projectile));
  }

  /**
   * Removes any game piece implementation from arena sets.
   */
  public synchronized void removePiece(GamePiece piece) {
    if (piece instanceof GamePieceOnFieldSimulation grounded) {
      gamePiecesOnField.remove(grounded);
    }
    if (piece instanceof GamePieceProjectile projectile) {
      gamePieceProjectiles.remove(projectile);
    }
  }

  /**
   * Returns all grounded pieces on field.
   */
  public synchronized Set<GamePieceOnFieldSimulation> gamePiecesOnField() {
    return Set.copyOf(gamePiecesOnField);
  }

  /**
   * Returns all projectile pieces currently in flight.
   */
  public synchronized Set<GamePieceProjectile> gamePieceLaunched() {
    return Set.copyOf(gamePieceProjectiles);
  }

  /**
   * Gets all piece poses filtered by type for visual publishing.
   */
  public synchronized List<Pose3> getGamePiecesPosesByType(String type) {
    List<Pose3> poses = new ArrayList<>();
    for (GamePieceOnFieldSimulation grounded : gamePiecesOnField) {
      if (grounded.type().equals(type)) {
        poses.add(grounded.pose());
      }
    }
    for (GamePieceProjectile projectile : gamePieceProjectiles) {
      if (projectile.type().equals(type)) {
        poses.add(projectile.pose());
      }
    }
    return List.copyOf(poses);
  }

  /**
   * Provides access to the underlying world for advanced integrations.
   */
  public PhysicsWorld world() {
    return world;
  }

  /**
   * Convenience helper for creating rigid body wrappers in arena context.
   */
  protected RigidBody createRigidBody(double massKg, Pose2 pose) {
    PhysicsBody body = world.createBody(massKg);
    body.setPosition(new Vec3(pose.xMeters(), pose.yMeters(), 0.0));
    body.setGravityEnabled(false);
    return new RigidBody(body);
  }

  private Pose2 defaultIntakePose() {
    if (driveTrainSimulations.isEmpty()) {
      return new Pose2(0.0, 0.0, 0.0);
    }
    return driveTrainSimulations.get(0).pose();
  }
}