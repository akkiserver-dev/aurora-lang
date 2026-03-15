package aurora.runtime;

/**
 * Represents an active exception handler in an execution frame.
 * It stores the target instruction pointer for the catch block and the stack
 * depth to restore when an exception is caught.
 */
public class ExceptionHandler {
    /** The instruction pointer where the catch block begins. */
    public final int catchPc;

    /** The depth of the operand stack when the try block was entered. */
    public final int stackDepth;
    public ExceptionHandler(int catchPc, int stackDepth) {
        this.catchPc = catchPc;
        this.stackDepth = stackDepth;
    }
}
