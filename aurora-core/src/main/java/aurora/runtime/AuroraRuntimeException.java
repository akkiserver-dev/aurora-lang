package aurora.runtime;

/**
 * Exception thrown when an error occurs during the execution of Aurora code.
 * This exception can wrap an Aurora {@link ArObject} representing the error value.
 */
public class AuroraRuntimeException extends RuntimeException {
    /** The Aurora object associated with this exception (e.g., a string message or error object). */
    public final ArObject auroraObject;

    /** The formatted Aurora stack trace at the point of the error. */
    private String auroraStackTrace;

    /**
     * Constructs a new runtime exception with a string message.
     * @param message The error message.
     */
    public AuroraRuntimeException(String message) {
        super(message);
        this.auroraObject = new ArString(message);
    }

    /**
     * Constructs a new runtime exception with a native Aurora object.
     * @param auroraObject The Aurora object representing the error.
     */
    public AuroraRuntimeException(ArObject auroraObject) {
        super(auroraObject.toString());
        this.auroraObject = auroraObject;
    }

    /**
     * Gets the Aurora-specific stack trace.
     * @return The stack trace string.
     */
    public String getAuroraStackTrace() {
        return auroraStackTrace;
    }

    /**
     * Sets the Aurora-specific stack trace.
     * @param trace The stack trace string to set.
     */
    public void setAuroraStackTrace(String trace) {
        this.auroraStackTrace = trace;
    }

    @Override
    public String getMessage() {
        String base = super.getMessage();
        if (auroraStackTrace != null) {
            return base + "\n" + auroraStackTrace;
        }
        return base;
    }
}
