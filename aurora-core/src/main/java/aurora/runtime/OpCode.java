package aurora.runtime;

/**
 * Defines the instruction set for the Aurora Virtual Machine.
 * Opcodes are divided into categories: stack operations, arithmetic, variables,
 * control flow, function calls, object management, and threading.
 */
public enum OpCode {
    /** Loads a constant from the chunk's constant pool onto the stack. */
    LOAD_CONST,
    /** Pops the top value from the stack and prints it. */
    PRINT,

    // Arithmetic and Logic
    /** Pops two values, adds them, and pushes the result. */
    ADD,
    /**
     * Pops two values, subtracts the second from the first, and pushes the result.
     */
    SUB,
    /** Pops two values, multiplies them, and pushes the result. */
    MUL,
    /** Pops two values, divides the first by the second, and pushes the result. */
    DIV,
    /** Pops two values, calculates the remainder, and pushes the result. */
    MOD,
    /** Pops a value, negates it (numeric), and pushes the result. */
    NEG,
    /** Pops a boolean value, inverts it, and pushes the result. */
    NOT,

    /** Pushes {@code true} onto the stack. */
    TRUE,
    /** Pushes {@code false} onto the stack. */
    FALSE,

    // Comparisons
    /**
     * Pops two values, pushes {@code true} if they are equal, {@code false}
     * otherwise.
     */
    EQUAL,
    /**
     * Pops two values, pushes {@code true} if they are not equal, {@code false}
     * otherwise.
     */
    NOT_EQUAL,
    /**
     * Pops two values, pushes {@code true} if the first is greater than the second.
     */
    GREATER,
    /**
     * Pops two values, pushes {@code true} if the first is greater than or equal to
     * the second.
     */
    GREATER_EQUAL,
    /**
     * Pops two values, pushes {@code true} if the first is less than the second.
     */
    LESS,
    /**
     * Pops two values, pushes {@code true} if the first is less than or equal to
     * the second.
     */
    LESS_EQUAL,

    // Variables
    /** Loads a local variable from the given register index onto the stack. */
    GET_LOCAL,
    /** Stores the top value from the stack into the given register index. */
    SET_LOCAL,
    /** Loads a value from the global environment by name. */
    GET_GLOBAL,
    /** Stores the top value from the stack into the global environment by name. */
    SET_GLOBAL,

    // Control Flow
    /** Unconditionally jumps to the target instruction address. */
    JUMP,
    /** Pops a boolean; jumps to the target address if it is {@code false}. */
    JUMP_IF_FALSE,

    // Function Calls
    /** Calls a function with a specified number of arguments. */
    CALL,
    /**
     * Returns from the current function, pushing the result to the caller's stack.
     */
    RETURN,

    // Objects and Classes
    /** Creates a new instance of a class. */
    NEW,
    /** Accesses a property of an instance or class. */
    GET_PROPERTY,
    /** Sets a property of an instance. */
    SET_PROPERTY,
    /** Invokes a method on an instance. */
    INVOKE,
    /** Invokes a method on the superclass. */
    SUPER_INVOKE,
    /** Accesses a property from the superclass. */
    SUPER_GET_PROPERTY,
    /** Sets a property on the superclass. */
    SUPER_SET_PROPERTY,

    // Arrays and Collections
    /** Creates a new array with the specified number of elements. */
    NEW_ARRAY,
    /** Gets an element at a specific index. */
    GET_INDEX,
    /** Sets an element at a specific index. */
    SET_INDEX,

    // Type Checks
    /**
     * Pops a value and a type; pushes {@code true} if the value is an instance of
     * the type.
     */
    IS,
    /** Pops a value and a type; casts the value to the type or throws an error. */
    AS,

    // Exception Handling
    /** Enters a try block. */
    TRY,
    /** Exits a try block. */
    END_TRY,
    /** Throws an exception. */
    THROW,

    // Concurrency
    /** Spawns a new thread to execute a function. */
    SPAWN_THREAD,

    // Iteration
    /** Checks if an iterator has more elements. */
    ITER_HAS_NEXT,
    /** Gets the next element from an iterator. */
    ITER_NEXT,

    /** Pops and discards the top value from the stack. */
    POP,

    /** Imports a module. */
    IMPORT,

    /** Stops the virtual machine. */
    HALT
}
