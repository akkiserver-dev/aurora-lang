package aurora.runtime;

/**
 * Represents a 32-bit integer value in the Aurora runtime.
 */
public class ArInt extends ArObject {
    /** The actual integer value. */
    public final int value;

    public ArInt(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
