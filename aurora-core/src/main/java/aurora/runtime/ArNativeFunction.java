package aurora.runtime;

import java.util.List;

/**
 * Represents a function implemented in Java that can be called from Aurora.
 */
public abstract class ArNativeFunction extends ArObject {
    /** The name of the native function. */
    public final String name;

    /** The number of parameters expected by the function. */
    public final int arity;

    public ArNativeFunction(String name, int arity) {
        this.name = name;
        this.arity = arity;
    }

    /**
     * Executes the native function with the given arguments.
     * @param args The arguments passed from Aurora.
     * @return The result of the function execution.
     */
    public abstract ArObject call(List<ArObject> args);

    @Override
    public String toString() {
        return "<native fn " + name + ">";
    }
}
