package aurora.runtime;

/**
 * Represents a boolean value in the Aurora runtime.
 * This class uses the singleton pattern for its {@code TRUE} and {@code FALSE} instances.
 */
public class ArBool extends ArObject {
    /** The actual boolean value. */
    public final boolean value;

    /** Singleton instance representing {@code true}. */
    public static final ArBool TRUE = new ArBool(true);

    /** Singleton instance representing {@code false}. */
    public static final ArBool FALSE = new ArBool(false);
    private ArBool(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
