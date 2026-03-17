package aurora.compiler;

import aurora.analyzer.AuroraDiagnostic;

import java.util.List;

/**
 * Thrown when one or more type errors are detected during compilation.
 * Carries the full list of {@link AuroraDiagnostic} objects so that callers
 * can format and display them however they like.
 */
public class TypeErrorException extends RuntimeException {

    private final List<AuroraDiagnostic> diagnostics;

    public TypeErrorException(List<AuroraDiagnostic> diagnostics) {
        super("Type error(s) detected");
        this.diagnostics = diagnostics;
    }

    public List<AuroraDiagnostic> getDiagnostics() {
        return diagnostics;
    }
}