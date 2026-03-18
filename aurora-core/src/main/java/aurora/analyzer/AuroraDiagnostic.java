package aurora.analyzer;

import aurora.parser.SourceLocation;

public record AuroraDiagnostic(SourceLocation location, String message, AuroraDiagnostic.Severity severity, String source) {
    public enum Severity {
        ERROR, WARNING, INFORMATION, HINT;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static AuroraDiagnostic error(SourceLocation loc, String message, String source) {
        return new AuroraDiagnostic(loc, message, Severity.ERROR, source);
    }

    public static AuroraDiagnostic warning(SourceLocation loc, String message, String source) {
        return new AuroraDiagnostic(loc, message, Severity.WARNING, source);
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + source + ": " + message
                + (location != null ? " @ " + location : "");
    }

    public static String formatDiagnostic(aurora.analyzer.AuroraDiagnostic d, String sourceCode) {
        final String RESET = "\u001B[0m";
        final String RED   = "\u001B[31m";
        final String GOLD  = "\u001B[33m";
        final String BOLD  = "\u001B[1m";
        final String CYAN  = "\u001B[36m";

        StringBuilder sb = new StringBuilder();

        // "error: <message>"
        sb.append(d.severity() == Severity.ERROR ? RED : GOLD).append(BOLD).append(d.severity()).append(RESET)
                .append(BOLD).append("[type]: ").append(d.message()).append(RESET).append("\n");

        if (d.location() == null) return sb.toString();

        int line   = d.location().line();
        int col    = d.location().column();    // 1-indexed
        int endCol = d.location().endColumn(); // 1-indexed, inclusive

        // "  --> file:line:col"
        sb.append(CYAN).append("  --> ").append(RESET)
                .append(d.location().sourceName()).append(":").append(line).append(":").append(col).append("\n");

        if (sourceCode == null) return sb.toString();

        String[] lines = sourceCode.split("\r?\n", -1);
        if (line < 1 || line > lines.length) return sb.toString();

        String srcLine   = lines[line - 1];
        String lineNum   = String.valueOf(line);
        String padding   = " ".repeat(lineNum.length());

        sb.append(CYAN).append(padding).append(" |").append(RESET).append("\n");
        sb.append(CYAN).append(lineNum).append(" | ").append(RESET).append(srcLine).append("\n");
        sb.append(CYAN).append(padding).append(" | ").append(RESET);

        int startIdx = col - 1;
        for (int i = 0; i < startIdx && i < srcLine.length(); i++) {
            sb.append(srcLine.charAt(i) == '\t' ? '\t' : ' ');
        }

        int caretLen = (endCol >= col) ? (endCol - col + 1) : 1;
        sb.append(RED).append(BOLD).append("^".repeat(caretLen)).append(RESET).append("\n");

        return sb.toString();
    }
}