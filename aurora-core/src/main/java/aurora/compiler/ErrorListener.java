package aurora.compiler;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * ANTLR4 error listener that collects syntax errors and translates them into LSP {@link Diagnostic} objects.
 * This class handles coordinate translation (1-based to 0-based) and provides user-friendly error messages.
 */
public class ErrorListener extends BaseErrorListener {
    /** A list of diagnostics collected during the parsing process. */
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    /**
     * Called by ANTLR when a syntax error is encountered.
     * Translates the error into an LSP Diagnostic and adds it to the list.
     *
     * @param recognizer         The recognizer that found the error.
     * @param offendingSymbol    The symbol that caused the error.
     * @param line               The 1-based line number.
     * @param charPositionInLine The 0-based character position in the line.
     * @param msg                The error message from ANTLR.
     * @param e                  The exception triggered by the error.
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {
        // ANTLR uses 1-based line, LSP uses 0-based line
        int lspLine = Math.max(0, line - 1);
        int lspCol = Math.max(0, charPositionInLine);

        // Try to figure out an end column from the offending token text
        int endCol = lspCol + 1;
        if (offendingSymbol instanceof org.antlr.v4.runtime.Token token) {
            String text = token.getText();
            if (text != null && !text.equals("<EOF>")) {
                int nlIdx = text.indexOf('\n');
                if (nlIdx != -1)
                    text = text.substring(0, nlIdx);
                endCol = lspCol + text.length();
                if (endCol == lspCol)
                    endCol = lspCol + 1;
            }
        }

        Diagnostic diag = new Diagnostic();
        diag.setRange(new Range(new Position(lspLine, lspCol), new Position(lspLine, endCol)));
        diag.setSeverity(DiagnosticSeverity.Error);
        diag.setSource("Aurora LSP");
        diag.setMessage(cleanMessage(msg));
        diagnostics.add(diag);

        //LspLogger.log("  syntax error at %d:%d — %s", line, charPositionInLine, msg);
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    private String cleanMessage(String msg) {
        if (msg == null)
            return "Syntax error";

        // Remove massive expecting {...} blocks
        int expectingIdx = msg.indexOf(" expecting {");
        if (expectingIdx != -1) {
            msg = msg.substring(0, expectingIdx) + " (unexpected token)";
        }

        // Clean up common ANTLR messages
        msg = msg.replace("extraneous input", "Unexpected token");
        msg = msg.replace("mismatched input", "Mismatched token");
        msg = msg.replace("no viable alternative at input", "Invalid syntax at");
        msg = msg.replace("missing", "Missing");

        return msg;
    }
}
