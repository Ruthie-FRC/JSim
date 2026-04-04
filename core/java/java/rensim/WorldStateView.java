package rensim;

import java.util.List;

/**
 * Read-only view of a physics world's state.
 *
 * <p>Provides thread-safe access to world state for rendering and analysis. All
 * returned views are immutable snapshots safe for concurrent access.
 */
public interface WorldStateView {
  /**
   * Returns the total number of bodies in the world.
   *
   * @return body count
   */
  int bodyCount();

  /**
   * Returns an immutable view of all bodies in the world.
   *
   * @return list of body state views
   */
  List<BodyStateView> bodies();

  /**
   * Finds a body by ID.
   *
   * @param bodyId the body identifier
   * @return state view if found, else null
   */
  BodyStateView findBody(int bodyId);

  /**
   * Returns the current gravity vector in meters per second squared.
   *
   * @return gravity vector
   */
  Vec3 gravity();

  /**
   * Returns the current simulation step count.
   *
   * @return step count
   */
  long stepCount();

  /**
   * Returns the accumulated simulation time in seconds.
   *
   * @return accumulated time
   */
  double accumulatedTimeSeconds();

  /**
   * Captures an immutable snapshot of the current world state.
   *
   * @return frame snapshot
   */
  FrameSnapshot captureFrame();
}
