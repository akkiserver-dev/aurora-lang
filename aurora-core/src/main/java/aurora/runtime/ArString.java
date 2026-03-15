package aurora.runtime;

/**
 * Represents a string of characters in the Aurora runtime.
 */
public class ArString extends ArObject {
    /** The actual string value. */
    public final String value;

    public ArString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
