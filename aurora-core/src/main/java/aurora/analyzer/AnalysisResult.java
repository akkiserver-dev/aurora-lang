package aurora.analyzer;

import aurora.analyzer.AuroraAnalyzer;
import aurora.analyzer.AuroraDiagnostic;
import aurora.parser.tree.Program;

import java.util.List;

/**
 * {@link AuroraAnalyzer#analyze} の返り値。
 * AST と診断リストをまとめて保持する。
 */
public final class AnalysisResult {

    /** パース済みの AST。パースが完全に失敗した場合は null になることがある。 */
    public final Program program;

    /** パース・型チェックで収集した診断のリスト。 */
    public final List<AuroraDiagnostic> diagnostics;

    public AnalysisResult(Program program, List<AuroraDiagnostic> diagnostics) {
        this.program     = program;
        this.diagnostics = diagnostics;
    }

    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(d -> d.severity == AuroraDiagnostic.Severity.ERROR);
    }
}