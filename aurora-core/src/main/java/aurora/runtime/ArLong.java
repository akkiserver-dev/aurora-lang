package aurora.runtime;

/**
 * Represents a 64-bit long integer value in the Aurora runtime.
 */
public class ArLong extends ArObject {
    /** The actual long value. */
    public final long value;

    public ArLong(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
