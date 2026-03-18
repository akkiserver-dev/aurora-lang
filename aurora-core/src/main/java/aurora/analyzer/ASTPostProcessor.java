package aurora.analyzer;

import aurora.parser.tree.*;
import aurora.parser.tree.decls.*;
import aurora.parser.tree.expr.*;
import aurora.parser.tree.stmt.*;

/**
 * Post-parse AST processor that runs after the ANTLR-based {@link aurora.parser.AuroraParser}
 * has produced a raw AST.
 *
 * <h2>Why this exists</h2>
 * The parser cannot perform type inference during the parse pass because the full AST is not
 * yet available—forward references and mutually recursive functions are not visible until
 * parsing is complete. This class encapsulates the two-phase post-processing that was
 * previously spread between {@link aurora.parser.AuroraParser#visitBlock} and
 * {@link TypeInferenceEngine}:
 *
 * <ol>
 *   <li><b>Phase 1 – Type Inference:</b> Run {@link TypeInferenceEngine#infer()} over the
 *       complete AST so that every {@link BlockStmt#returnType} is annotated with the
 *       correct {@link TypeNode}.</li>
 *   <li><b>Phase 2 – ReturnStmt Rewrite:</b> Walk every {@link BlockStmt} whose inferred
 *       {@code returnType} is <em>non-void</em> and rewrite the trailing
 *       {@link ExprStmt} into a {@link ControlStmt.ReturnStmt}.
 *       Loop bodies are always void and are therefore skipped for the rewrite
 *       (but are still descended into so that inner functions are processed).</li>
 * </ol>
 *
 * <p><b>Note on {@link TypeInferenceEngine#isVoid}:</b>
 * This method only checks whether the supplied {@link TypeNode} is itself void/null — it does
 * <em>not</em> perform any inference. Inference happens exclusively in Phase 1 above.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * Program program = AuroraParser.parse(source, sourceName);
 * // AuroraParser.parse() already calls ASTPostProcessor.process(program) internally,
 * // so manual invocation is only needed when constructing a Program outside the parser.
 * }</pre>
 */
public final class ASTPostProcessor {

    private ASTPostProcessor() {}

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Runs both post-processing phases on {@code program}.
     *
     * @param program the freshly parsed AST root
     */
    public static void process(Program program) {
        process(program, null);
    }

    /**
     * Runs both post-processing phases on {@code program} using the supplied
     * {@link ModuleResolver} for cross-module type lookups.
     *
     * @param program the freshly parsed AST root
     * @param modules module resolver (may be {@code null})
     */
    public static void process(Program program, ModuleResolver modules) {
        if (program == null) return;

        // Phase 1: annotate every BlockStmt.returnType via full-AST type inference
        TypeInferenceEngine engine = new TypeInferenceEngine(program, modules);
        engine.infer();

        // Phase 2: rewrite trailing ExprStmt -> ReturnStmt for non-void blocks
        rewriteProgram(program);
    }

    // -----------------------------------------------------------------------
    // Phase 2 - ReturnStmt rewrite walk
    // -----------------------------------------------------------------------

    private static void rewriteProgram(Program program) {
        if (program.statements == null) return;
        for (Statement s : program.statements) {
            rewriteStatement(s);
        }
    }

    private static void rewriteStatement(Statement stmt) {
        switch (stmt) {
            case FunctionDecl     d -> rewriteFunctionDecl(d);
            case ThreadDecl       d -> rewriteBlock(d.body);
            case ClassDecl        d -> rewriteClassDecl(d);
            case RecordDecl       d -> rewriteRecordDecl(d);
            case BlockStmt        b -> rewriteBlock(b);
            case IfStmt           s -> rewriteIfStmt(s);
            case LoopStmt         s -> rewriteLoopStmt(s);
            case TryStmt          s -> rewriteTryStmt(s);
            case MatchStmt        s -> rewriteMatchStmt(s);
            case InitializerBlock s -> rewriteBlock(s.body);
            case ExprStmt         s -> rewriteExpr(s.expr);
            default                 -> { /* leaf - nothing to rewrite */ }
        }
    }

    // ----- Declaration rewrites -----

    private static void rewriteFunctionDecl(FunctionDecl d) {
        // Expression-body functions (fun foo() = expr) already have a ReturnStmt
        // injected by the parser at parse time; only block-body functions need rewriting.
        if (d.body != null && !d.isExpressionBody) {
            rewriteBlock(d.body);
        }
    }

    private static void rewriteClassDecl(ClassDecl d) {
        if (d.members == null) return;
        for (Declaration m : d.members) {
            switch (m) {
                case FunctionDecl    f -> rewriteFunctionDecl(f);
                case ConstructorDecl c -> rewriteBlock(c.body);
                // InitializerBlock is a Statement - it appears inside classMember but is
                // treated as a Statement in the AST. It will be visited via
                // rewriteStatement() if it ever appears in a block's statement list.
                default -> { /* no-op */ }
            }
        }
    }

    private static void rewriteRecordDecl(RecordDecl d) {
        if (d.members == null) return;
        for (Declaration m : d.members) {
            if (m instanceof FunctionDecl f) rewriteFunctionDecl(f);
        }
    }

    // ----- Block rewrite (the core of Phase 2) -----

    /**
     * Recursively descends into {@code block} depth-first, then - if the block's inferred
     * {@link BlockStmt#returnType} is non-void - replaces the trailing {@link ExprStmt}
     * with a {@link ControlStmt.ReturnStmt}.
     *
     * <p>{@link TypeInferenceEngine#isVoid} only inspects the {@link TypeNode} value that
     * was written by Phase 1; it performs no inference of its own.
     */
    private static void rewriteBlock(BlockStmt block) {
        if (block == null || block.statements == null || block.statements.isEmpty()) return;

        // Depth-first: process nested statements before touching the tail.
        for (Statement s : block.statements) {
            rewriteStatement(s);
        }

        // Rewrite the tail only when the block has a non-void inferred type.
        if (!TypeInferenceEngine.isVoid(block.returnType)) {
            int last = block.statements.size() - 1;
            if (block.statements.get(last) instanceof ExprStmt exprStmt) {
                block.statements.set(last,
                        new ControlStmt.ReturnStmt(exprStmt.loc, exprStmt.expr));
            }
        }
    }

    // ----- Expression descent (for lambdas / if-exprs embedded in expressions) -----

    /**
     * Visits expressions that may contain nested blocks (lambdas, if-expressions, calls
     * with lambda arguments) so those inner blocks are also rewritten.
     */
    private static void rewriteExpr(Expr expr) {
        if (expr == null) return;
        switch (expr) {
            case LambdaExpr e -> {
                // Lambda body is a Node; it is either a BlockStmt (needs rewriting) or a
                // bare Expr that was already wrapped in a ReturnStmt by the parser.
                if (e.body instanceof BlockStmt b) {
                    rewriteBlock(b);
                }
            }
            case IfExpr e -> {
                if (e.thenBlock instanceof BlockStmt b1) rewriteBlock(b1);
                if (e.elseBlock instanceof BlockStmt b2) rewriteBlock(b2);
            }
            case CallExpr e -> {
                // Descend into arguments - they may contain lambda literals.
                if (e.arguments != null) {
                    for (CallExpr.Argument arg : e.arguments) {
                        rewriteExpr(arg.value);
                    }
                }
                // Also descend into the callee (e.g. method chain returns a lambda).
                rewriteExpr(e.callee);
            }
            case BinaryExpr e -> { rewriteExpr(e.left);    rewriteExpr(e.right); }
            case UnaryExpr  e -> rewriteExpr(e.operand);
            case AccessExpr e -> rewriteExpr(e.object);
            case CastExpr   e -> rewriteExpr(e.expr);
            case IndexExpr  e -> { rewriteExpr(e.object);  rewriteExpr(e.index); }
            case ElvisExpr  e -> { rewriteExpr(e.left);    rewriteExpr(e.right); }
            case ArrayExpr  e -> { if (e.elements != null) e.elements.forEach(ASTPostProcessor::rewriteExpr); }
            default           -> { /* leaf */ }
        }
    }

    // ----- Other statement rewrites -----

    private static void rewriteIfStmt(IfStmt s) {
        if (s.thenBlock != null) rewriteBlock(s.thenBlock);
        if (s.elseIfs   != null) s.elseIfs.forEach(ei -> rewriteBlock(ei.block));
        if (s.elseBlock != null) rewriteBlock(s.elseBlock);
    }

    private static void rewriteLoopStmt(LoopStmt s) {
        // Loop bodies are always void - no ReturnStmt rewrite.
        // But we still descend so that inner functions / lambdas are processed.
        if (s.body != null) {
            for (Statement inner : s.body.statements) {
                rewriteStatement(inner);
            }
        }
    }

    private static void rewriteTryStmt(TryStmt s) {
        if (s.tryBlock     != null) rewriteBlock(s.tryBlock);
        if (s.catches      != null) s.catches.forEach(c -> rewriteBlock(c.block()));
        if (s.finallyBlock != null) rewriteBlock(s.finallyBlock);
    }

    private static void rewriteMatchStmt(MatchStmt s) {
        if (s.cases == null) return;
        for (MatchStmt.MatchCase c : s.cases) {
            // MatchCase.body is Node - it can be either a BlockStmt or a bare Expr.
            if (c.body instanceof BlockStmt b) {
                rewriteBlock(b);
            } else if (c.body instanceof Expr e) {
                rewriteExpr(e);
            }
        }
    }
}