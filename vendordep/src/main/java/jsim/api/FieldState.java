package jsim.api;

/**
 * A Read/Write proxy wrapper safeguarding underlying JSim state data.
 * All access forces adherence to StateManager validation logic.
 *
 * @param <T> The type of the tracked state object.
 */
public class FieldState<T> {
    private final T state;

    protected FieldState(T state) {
        this.state = state;
    }

    /**
     * Safely reads the snapshot of the state.
     * @return the tracked state.
     */
    public T get() {
        return state;
    }
}
