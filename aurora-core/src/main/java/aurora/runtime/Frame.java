package aurora.runtime;

import java.util.ArrayList;
import java.util.List;

import java.util.Stack;

/**
 * Represents a single execution frame in the Aurora VM stack.
 * Each frame corresponds to a function call and maintains its own set of local
 * variable registers, instruction pointer, and exception handlers.
 */
public class Frame {
    /** The registers used for local variables and temporary values. */
    private ArObject[] registers = new ArObject[256];

    /** A list used to store registers if they exceed the fixed array size. */
    private List<ArObject> spilledRegisters = null;

    /** The stack of exception handlers currently active within this frame. */
    public final Stack<ExceptionHandler> handlers = new Stack<>();

    /** The current instruction pointer (program counter). */
    public int pc = 0;

    /** The code chunk being executed in this frame. */
    public final Chunk chunk;

    /** The function associated with this frame. */
    public final ArFunction function;

    /** The previous frame in the call stack. */
    public Frame prev;

    /** The number of arguments passed to this function. */
    public int argCount;

    /** Indicates whether this frame is executing a class initializer (constructor). */
    public boolean isInitializer = false;

    /**
     * Constructs a new execution frame.
     * @param chunk The code chunk to execute.
     * @param function The function being called.
     * @param prev The caller's frame.
     */
    public Frame(Chunk chunk, ArFunction function, Frame prev) {
        this.chunk = chunk;
        this.function = function;
        this.prev = prev;
    }

    public ArObject get(int index) {
        if (index < 256) {
            return registers[index];
        }
        return (spilledRegisters != null && index - 256 < spilledRegisters.size())
                ? spilledRegisters.get(index - 256) : null;
    }

    public void set(int index, ArObject value) {
        if (index < 256) {
            registers[index] = value;
        } else {
            if (spilledRegisters == null) spilledRegisters = new ArrayList<>();
            int spillIndex = index - 256;
            while (spilledRegisters.size() <= spillIndex) spilledRegisters.add(null);
            spilledRegisters.set(spillIndex, value);
        }
    }
}
