package aurora.runtime;

/**
 * Represents a 64-bit double-precision floating-point value in the Aurora runtime.
 */
public class ArDouble extends ArObject {
    /** The actual double value. */
    public final double value;

    public ArDouble(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
