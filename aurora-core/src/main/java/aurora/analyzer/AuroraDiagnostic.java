package aurora.analyzer;

import aurora.parser.SourceLocation;

/**
 * LSP4J に依存しない共通の診断情報型。
 * LSP では {@code Diagnostic} に、IntelliJ では {@code HighlightSeverity} にそれぞれ変換して使う。
 */
public final class AuroraDiagnostic {

    public enum Severity {
        ERROR, WARNING, INFORMATION, HINT
    }

    /** エラーが発生したソース上の位置 (null の場合はファイル先頭扱い) */
    public final SourceLocation location;

    /** 診断メッセージ */
    public final String message;

    /** 深刻度 */
    public final Severity severity;

    /** 診断のソース識別子 (例: "Aurora Parser", "Aurora TypeChecker") */
    public final String source;

    public AuroraDiagnostic(SourceLocation location, String message, Severity severity, String source) {
        this.location = location;
        this.message  = message;
        this.severity = severity;
        this.source   = source;
    }

    /** 簡易ファクトリ: ERROR */
    public static AuroraDiagnostic error(SourceLocation loc, String message, String source) {
        return new AuroraDiagnostic(loc, message, Severity.ERROR, source);
    }

    /** 簡易ファクトリ: WARNING */
    public static AuroraDiagnostic warning(SourceLocation loc, String message, String source) {
        return new AuroraDiagnostic(loc, message, Severity.WARNING, source);
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + source + ": " + message
                + (location != null ? " @ " + location : "");
    }
}