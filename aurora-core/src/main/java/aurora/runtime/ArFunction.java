package aurora.runtime;

/**
 * Represents a user-defined function in the Aurora runtime.
 * Contains the code chunk and metadata required for execution.
 */
public class ArFunction extends ArObject {
    /** The name of the function. */
    public final String name;

    /** The compiled bytecode chunk for this function. */
    public final Chunk chunk;

    /** The number of parameters this function expects. */
    public final int arity;

    /** The class that owns this function, if it is a method. */
    public ArClass ownerClass = null;

    public ArFunction(String name, Chunk chunk, int arity) {
        this.name = name;
        this.chunk = chunk;
        this.arity = arity;
    }

    @Override
    public String toString() {
        return "<fn " + name + ">";
    }
}
