package aurora.runtime;

/**
 * Represents a 32-bit floating-point value in the Aurora runtime.
 */
public class ArFloat extends ArObject {
    /** The actual float value. */
    public final float value;

    public ArFloat(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
