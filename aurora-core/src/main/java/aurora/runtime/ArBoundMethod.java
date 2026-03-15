package aurora.runtime;

/**
 * Represents a method that has been bound to a specific receiver instance.
 * When called, the receiver is automatically passed as the {@code self} argument.
 */
public class ArBoundMethod extends ArObject {
    /** The instance to which the method is bound. */
    public final ArObject receiver;

    /** The underlying function or method. */
    public final ArObject method;

    public ArBoundMethod(ArObject receiver, ArObject method) {
        this.receiver = receiver;
        this.method = method;
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
