package rensim;

/**
 * Listener for collision contacts emitted during world stepping.
 */
@FunctionalInterface
public interface CollisionListener {
  /**
   * Called when a contact is resolved by the Java-side collision pass.
   *
   * @param contact collision event data
   */
  void onContact(CollisionContact contact);

  /**
   * Returns a listener that performs no operation.
   *
   * @return no-op listener
   */
  static CollisionListener noop() {
    return contact -> {};
  }
}