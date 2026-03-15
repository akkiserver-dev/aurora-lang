package aurora.runtime;

/**
 * Wraps a native Java object so it can be handled within the Aurora runtime.
 */
public class ArNativeObject extends ArObject {
    /** The underlying Java object being wrapped. */
    public final Object object;

    public ArNativeObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "<native " + object.getClass().getSimpleName() + ">";
    }
}
