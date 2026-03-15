package aurora.runtime;

/**
 * The base class for all objects and values in the Aurora runtime.
 * Every piece of data handled by the Virtual Machine is an instance of {@code ArObject}.
 */
public abstract class ArObject {
    @Override
    public abstract String toString();
}
