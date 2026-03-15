package aurora.runtime;

/**
 * Represents the absence of a value (none) in the Aurora runtime.
 * This class uses the singleton pattern.
 */
public class ArNone extends ArObject {
    /** The singleton instance of ArNone. */
    public static final ArNone INSTANCE = new ArNone();
    private ArNone() {}

    @Override
    public String toString() {
        return "none";
    }
}
