package aurora.compiler;

import aurora.lsp.ModuleResolver;
import aurora.parser.tree.*;
import aurora.parser.tree.decls.*;
import aurora.parser.tree.expr.*;
import aurora.parser.tree.stmt.*;
import aurora.parser.tree.util.BinaryOperator;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A static analysis pass that evaluates type safety, focusing on nullability and basic inheritance.
 * This class implements a {@link NodeVisitor} that traverses the AST and collects type information,
 * reporting errors as {@link Diagnostic} objects for use in the CLI or LSP.
 */
public class TypeChecker implements NodeVisitor<TypeNode> {
    /** A list of diagnostics (errors and warnings) found during analysis. */
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    /** A stack of symbol tables representing nested scopes. */
    private final List<Map<String, TypeNode>> scopes = new ArrayList<>();

    /** A map of global names to their declarations. */
    private final Map<String, Declaration> globals = new HashMap<>();

    /** The module resolver used to lookup types in other files. */
    private final ModuleResolver modules;

    /** The program AST root currently being checked. */
    private final Program currentProgram;

    /** Built-in "Any" type. */
    private final TypeNode ANY;

    /** Built-in "none" (null) type. */
    private final TypeNode NONE;

    /**
     * Initializes a new TypeChecker for the specified program.
     *
     * @param currentProgram The AST root to analyze.
     * @param modules        The resolver for module-level lookups.
     */
    public TypeChecker(Program currentProgram, ModuleResolver modules) {
        this.currentProgram = currentProgram;
        this.modules = modules;
        this.ANY = new TypeNode(new aurora.parser.SourceLocation(), "Any");
        this.NONE = new TypeNode(new aurora.parser.SourceLocation(), "none");
    }

    /**
     * Returns the list of diagnostics collected during analysis.
     *
     * @return A list of {@link Diagnostic} objects.
     */
    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    /**
     * Reports a type error at the specified node's location.
     *
     * @param node    The AST node where the error occurred.
     * @param message The error message.
     */
    public void reportError(Node node, String message) {
        Diagnostic diag = new Diagnostic();
        int line = Math.max(0, node.loc.line() - 1);
        int col = Math.max(0, node.loc.column());
        diag.setRange(new Range(new Position(line, col), new Position(line, col + 1))); // Quick range
        diag.setSeverity(DiagnosticSeverity.Error);
        diag.setSource("Aurora TypeChecker");
        diag.setMessage(message);
        diagnostics.add(diag);
    }

    public Map<String, Declaration> getGlobals() {
        return globals;
    }

    /**
     * Pushes a new symbolic scope onto the stack.
     */
    private void beginScope() {
        scopes.add(new HashMap<>());
    }

    /**
     * Pops the current symbolic scope from the stack.
     */
    private void endScope() {
        if (!scopes.isEmpty()) {
            scopes.removeLast();
        }
    }

    /**
     * Declares a variable in the current scope with the specified type.
     *
     * @param name The name of the variable.
     * @param type The type of the variable.
     */
    private void declareVariable(String name, TypeNode type) {
        if (!scopes.isEmpty()) {
            scopes.getLast().put(name, type != null ? type : ANY);
        }
    }

    /**
     * Resolves the type of a variable by searching through nested scopes.
     *
     * @param name The name of the variable to resolve.
     * @return The variable's type, or {@link #ANY} if not found.
     */
    private TypeNode resolveVariable(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        return ANY;
    }

    /**
     * Determines if a value of one type can be assigned to a variable of another type.
     * Considers "Any", "none" (nullability), and inheritance.
     *
     * @param target The target type.
     * @param value  The value type being assigned.
     * @return {@code true} if the assignment is valid.
     */
    private boolean isAssignable(TypeNode target, TypeNode value) {
        if (target == null || value == null)
            return true;

        // Everything is assignable if Any is involved in this simplistic checker
        if (target.name.equals("Any") || value.name.equals("Any"))
            return true;

        // Nullability check: if value is "none", target must be nullable or "none"
        if (value.name.equals("none")) {
            boolean isTargetNullable = target.suffixes.stream()
                    .anyMatch(s -> s instanceof TypeNode.TypeSuffix.Nullable);
            return isTargetNullable || target.name.equals("none");
        }

        // For simplicity, we just check names and nullability
        if (target.name.equals(value.name) || target.name.equals("object")) {
            return true;
        }

        // Check inheritance
        Node valDecl = aurora.lsp.SymbolResolver.resolveTypeName(currentProgram, value.name, modules);
        return inherits(valDecl, target.name);
    }

    /**
     * Recursively checks if a declaration inherits from a target type name.
     *
     * @param decl       The declaration to check (Class, Record, or Interface).
     * @param targetName The name of the potential parent type.
     * @return {@code true} if the declaration inherits from the target.
     */
    private boolean inherits(Node decl, String targetName) {
        switch (decl) {
            case null -> {
                return false;
            }
            case ClassDecl cls -> {
                if (cls.name.equals(targetName))
                    return true;
                if (cls.superClass != null
                        && inherits(aurora.lsp.SymbolResolver.resolveTypeName(currentProgram, cls.superClass.name, modules),
                        targetName))
                    return true;
                if (cls.interfaces != null) {
                    for (TypeNode iface : cls.interfaces) {
                        if (inherits(aurora.lsp.SymbolResolver.resolveTypeName(currentProgram, iface.name, modules),
                                targetName))
                            return true;
                    }
                }
            }
            case RecordDecl rec -> {
                if (rec.name.equals(targetName))
                    return true;
                if (rec.implementsInterfaces != null) {
                    for (TypeNode iface : rec.implementsInterfaces) {
                        if (inherits(aurora.lsp.SymbolResolver.resolveTypeName(currentProgram, iface.name, modules),
                                targetName))
                            return true;
                    }
                }
            }
            case InterfaceDecl iface -> {
                if (iface.name.equals(targetName))
                    return true;
                if (iface.interfaces != null) {
                    for (TypeNode parent : iface.interfaces) {
                        if (inherits(aurora.lsp.SymbolResolver.resolveTypeName(currentProgram, parent.name, modules),
                                targetName))
                            return true;
                    }
                }
            }
            default -> {
            }
        }

        return false;
    }

    // --- VISITOR METHODS ---
    @Override
    public TypeNode visitNode(Node node) {
        return ANY;
    }

    @Override
    public TypeNode visitProgram(Program program) {
        // Collect globals
        for (Statement stmt : program.statements) {
            if (stmt instanceof Declaration decl) {
                globals.put(decl.name, decl);
            }
        }

        for (Statement stmt : program.statements) {
            visitStatement(stmt);
        }
        return ANY;
    }

    @Override
    public TypeNode visitProgramPackage(Program.Package pkg) {
        return ANY;
    }

    @Override
    public TypeNode visitProgramImport(Program.Import imp) {
        return ANY;
    }

    @Override
    public TypeNode visitProgramImportWildCard(Program.ImportWildCard imp) {
        return ANY;
    }

    @Override
    public TypeNode visitProgramImportAlias(Program.ImportAlias imp) {
        return ANY;
    }

    @Override
    public TypeNode visitProgramImportMulti(Program.ImportMulti imp) {
        return ANY;
    }

    @Override
    public TypeNode visitTypeNode(TypeNode type) {
        return type;
    }

    @Override
    public TypeNode visitTypeNodeLambda(TypeNode.Lambda lambda) {
        return lambda;
    }

    @Override
    public TypeNode visitStatement(Statement stmt) {
        if (stmt instanceof ExprStmt s)
            return visitExprStmt(s);
        if (stmt instanceof BlockStmt s)
            return visitBlockStmt(s);
        if (stmt instanceof IfStmt s)
            return visitIfStmt(s);
        if (stmt instanceof FieldDecl s)
            return visitFieldDecl(s);
        if (stmt instanceof FunctionDecl s)
            return visitFunctionDecl(s);
        if (stmt instanceof ClassDecl s)
            return visitClassDecl(s);
        if (stmt instanceof EnumDecl s)
            return visitEnumDecl(s);
        if (stmt instanceof RecordDecl s)
            return visitRecordDecl(s);
        if (stmt instanceof InterfaceDecl s)
            return visitInterfaceDecl(s);
        if (stmt instanceof LoopStmt s)
            return visitLoopStmt(s);
        if (stmt instanceof ControlStmt s)
            return visitControlStmt(s);
        return ANY;
    }

    @Override
    public TypeNode visitBlockStmt(BlockStmt stmt) {
        beginScope();
        for (Statement s : stmt.statements) {
            visitStatement(s);
        }
        endScope();
        return ANY;
    }

    @Override
    public TypeNode visitExprStmt(ExprStmt stmt) {
        if (stmt.expr != null)
            visitExpr(stmt.expr);
        return ANY;
    }

    @Override
    public TypeNode visitIfStmt(IfStmt stmt) {
        visitExpr(stmt.condition);
        visitBlockStmt(stmt.thenBlock);
        if (stmt.elseIfs != null) {
            for (IfStmt.ElseIf e : stmt.elseIfs)
                visitIfStmtElseIf(e);
        }
        if (stmt.elseBlock != null)
            visitBlockStmt(stmt.elseBlock);
        return ANY;
    }

    @Override
    public TypeNode visitIfStmtElseIf(IfStmt.ElseIf elseif) {
        visitExpr(elseif.condition);
        visitBlockStmt(elseif.block);
        return ANY;
    }

    @Override
    public TypeNode visitLoopStmt(LoopStmt stmt) {
        if (stmt instanceof LoopStmt.WhileStmt s)
            return visitWhileStmt(s);
        if (stmt instanceof LoopStmt.RepeatUntilStmt s)
            return visitRepeatUntilStmt(s);
        if (stmt instanceof LoopStmt.ForStmt s)
            return visitForStmt(s);
        return ANY;
    }

    @Override
    public TypeNode visitWhileStmt(LoopStmt.WhileStmt stmt) {
        visitExpr(stmt.condition);
        visitBlockStmt(stmt.body);
        return ANY;
    }

    @Override
    public TypeNode visitRepeatUntilStmt(LoopStmt.RepeatUntilStmt stmt) {
        visitBlockStmt(stmt.body);
        visitExpr(stmt.condition);
        return ANY;
    }

    @Override
    public TypeNode visitForStmt(LoopStmt.ForStmt stmt) {
        beginScope();
        declareVariable(stmt.varName, ANY);
        visitExpr(stmt.iterable);
        visitBlockStmt(stmt.body);
        endScope();
        return ANY;
    }

    @Override
    public TypeNode visitTryStmt(TryStmt stmt) {
        visitBlockStmt(stmt.tryBlock);
        for (TryStmt.CatchClause c : stmt.catches)
            visitTryStmtCatch(c);
        if (stmt.finallyBlock != null)
            visitBlockStmt(stmt.finallyBlock);
        return ANY;
    }

    @Override
    public TypeNode visitTryStmtCatch(TryStmt.CatchClause catchClause) {
        beginScope();
        declareVariable(catchClause.var(), catchClause.type());
        visitBlockStmt(catchClause.block());
        endScope();
        return ANY;
    }

    @Override
    public TypeNode visitMatchStmt(MatchStmt stmt) {
        return ANY;
    }

    @Override
    public TypeNode visitMatchCase(MatchStmt.MatchCase matchCase) {
        return ANY;
    }

    @Override
    public TypeNode visitMatchPattern(MatchStmt.Pattern pattern) {
        return ANY;
    }

    @Override
    public TypeNode visitLiteralPattern(MatchStmt.LiteralPattern pattern) {
        return ANY;
    }

    @Override
    public TypeNode visitRangePattern(MatchStmt.RangePattern pattern) {
        return ANY;
    }

    @Override
    public TypeNode visitIsPattern(MatchStmt.IsPattern pattern) {
        return ANY;
    }

    @Override
    public TypeNode visitIdentifierPattern(MatchStmt.IdentifierPattern pattern) {
        return ANY;
    }

    @Override
    public TypeNode visitDestructurePattern(MatchStmt.DestructurePattern pattern) {
        return ANY;
    }

    @Override
    public TypeNode visitDefaultPattern(MatchStmt.DefaultPattern pattern) {
        return ANY;
    }

    @Override
    public TypeNode visitLabeledStmt(LabeledStmt stmt) {
        return ANY;
    }

    @Override
    public TypeNode visitInitializerBlock(InitializerBlock stmt) {
        return ANY;
    }

    @Override
    public TypeNode visitControlStmt(ControlStmt stmt) {
        return ANY;
    }

    @Override
    public TypeNode visitReturnStmt(ControlStmt.ReturnStmt stmt) {
        if (stmt.value != null)
            visitExpr(stmt.value);
        return ANY;
    }

    @Override
    public TypeNode visitThrowStmt(ControlStmt.ThrowStmt stmt) {
        visitExpr(stmt.value);
        return ANY;
    }

    @Override
    public TypeNode visitBreakStmt(ControlStmt.BreakStmt stmt) {
        return ANY;
    }

    @Override
    public TypeNode visitContinueStmt(ControlStmt.ContinueStmt stmt) {
        return ANY;
    }

    @Override
    public TypeNode visitDeclaration(Declaration decl) {
        return ANY;
    }

    @Override
    public TypeNode visitErrorNode(ErrorNode err) {
        return ANY;
    }

    @Override
    public TypeNode visitClassDecl(ClassDecl decl) {
        beginScope(); // Class scope
        if (decl.members != null) {
            for (Declaration member : decl.members) {
                if (member instanceof FieldDecl field) {
                    declareVariable(field.name, field.type);
                    if (field.init != null) {
                        TypeNode initType = visitExpr(field.init);
                        if (!isAssignable(field.type, initType)) {
                            reportError(field, "Cannot assign type '" + initType + "' to '" + field.type + "'");
                        }
                    }
                } else if (member instanceof FunctionDecl func) {
                    visitFunctionDecl(func);
                } else if (member instanceof ConstructorDecl cons) {
                    visitConstructorDecl(cons);
                }
            }
        }
        endScope();
        return ANY;
    }

    @Override
    public TypeNode visitFieldDecl(FieldDecl decl) {
        declareVariable(decl.name, decl.type);
        if (decl.init != null) {
            TypeNode initType = visitExpr(decl.init);
            if (!isAssignable(decl.type, initType)) {
                reportError(decl, "Cannot assign type '" + initType + "' to '" + decl.type + "'");
            }
        }
        return ANY;
    }

    @Override
    public TypeNode visitFunctionDecl(FunctionDecl decl) {
        beginScope();
        if (decl.params != null) {
            for (ParamDecl p : decl.params)
                declareVariable(p.name, p.type);
        }
        if (decl.body != null) {
            visitBlockStmt(decl.body);
        }
        endScope();
        return ANY;
    }

    @Override
    public TypeNode visitConstructorDecl(ConstructorDecl decl) {
        beginScope();
        if (decl.params != null) {
            for (ParamDecl p : decl.params)
                declareVariable(p.name, p.type);
        }
        if (decl.body instanceof BlockStmt b) {
            visitBlockStmt(b);
        }
        endScope();
        return ANY;
    }

    @Override
    public TypeNode visitClassParamDecl(ClassParamDecl decl) {
        return ANY;
    }

    @Override
    public TypeNode visitEnumDecl(EnumDecl decl) {
        return ANY;
    }

    @Override
    public TypeNode visitEnumMember(EnumDecl.EnumMember member) {
        return ANY;
    }

    @Override
    public TypeNode visitThreadDecl(ThreadDecl decl) {
        return ANY;
    }

    @Override
    public TypeNode visitRecordDecl(RecordDecl decl) {
        return ANY;
    }

    @Override
    public TypeNode visitParamDecl(ParamDecl decl) {
        return ANY;
    }

    @Override
    public TypeNode visitMethodSignature(MethodSignature sig) {
        return ANY;
    }

    @Override
    public TypeNode visitInterfaceDecl(InterfaceDecl decl) {
        return ANY;
    }

    @Override
    public TypeNode visitExpr(Expr expr) {
        if (expr instanceof LiteralExpr e)
            return visitLiteralExpr(e);
        if (expr instanceof BinaryExpr e)
            return visitBinaryExpr(e);
        if (expr instanceof UnaryExpr e)
            return visitUnaryExpr(e);
        if (expr instanceof CallExpr e)
            return visitCallExpr(e);
        if (expr instanceof AccessExpr e)
            return visitAccessExpr(e);
        if (expr instanceof SelfExpr e)
            return visitSelfExpr(e);
        // Add others as needed
        return ANY;
    }

    @Override
    public TypeNode visitLiteralExpr(LiteralExpr expr) {
        return switch (expr.type) {
            case INT -> new TypeNode(expr.loc, "int");
            case STRING -> new TypeNode(expr.loc, "string");
            case NULL -> NONE;
            case BOOL -> new TypeNode(expr.loc, "bool");
            case FLOAT -> new TypeNode(expr.loc, "float");
            case DOUBLE -> new TypeNode(expr.loc, "double");
            case LONG -> new TypeNode(expr.loc, "long");
        };
    }

    @Override
    public TypeNode visitBinaryExpr(BinaryExpr expr) {
        TypeNode left = visitExpr(expr.left);
        TypeNode right = visitExpr(expr.right);

        if (expr.op == BinaryOperator.IN) {
            // 'in' operator evaluates to a boolean
            return new TypeNode(expr.loc, "bool");
        }

        if (expr.op == BinaryOperator.ASSIGN) {
            if (!isAssignable(left, right)) {
                reportError(expr, "Cannot assign type '" + right + "' to '" + left + "'");
            }
            return right;
        }
        return left;
    }

    @Override
    public TypeNode visitCallExpr(CallExpr expr) {
        // Evaluate args
        for (CallExpr.Argument arg : expr.arguments) {
            visitExpr(arg.value);
        }
        // Simplified checking
        return ANY;
    }

    /*
    @Override
    public TypeNode visitNewExpr(NewExpr expr) {
        List<TypeNode> argTypes = new ArrayList<>();
        for (CallExpr.Argument arg : expr.arguments) {
            argTypes.add(visitExpr(arg.value));
        }

        // Try to find the class
        Declaration decl = globals.get(expr.type.name);
        if (decl instanceof ClassDecl cls) {
            for (Declaration member : cls.members) {
                if (member instanceof ConstructorDecl cons) {
                    // Check params
                    if (cons.params.size() == argTypes.size()) {
                        for (int i = 0; i < cons.params.size(); i++) {
                            TypeNode expectedType = cons.params.get(i).type;
                            TypeNode actualType = argTypes.get(i);
                            
                            // Simple generic bypass for now: if expected is a single uppercase letter, assume it's a generic parameter
                            boolean isGeneric = expectedType.name.length() == 1 && Character.isUpperCase(expectedType.name.charAt(0));
                            
                            if (!isGeneric && !isAssignable(expectedType, actualType)) {
                                reportError(expr.arguments.get(i).value,
                                        "Argument " + (i + 1) + " expects '" + expectedType + "', but got '"
                                                + actualType + "'");
                            }
                        }
                    }
                }
            }
        }
        return new TypeNode(expr.loc, expr.type.name);
    }
    */

    @Override
    public TypeNode visitAccessExpr(AccessExpr expr) {
        if (expr.object == null) {
            return resolveVariable(expr.member);
        }
        return ANY; // Simplified
    }

    @Override
    public TypeNode visitUnaryExpr(UnaryExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitLambdaExpr(LambdaExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitIndexExpr(IndexExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitIfExpr(IfExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitElvisExpr(ElvisExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitCastExpr(CastExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitMatchExpr(MatchExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitRangeExpr(RangeExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitTypeCheckExpr(TypeCheckExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitThreadExpr(ThreadExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitSelfExpr(SelfExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitSuperExpr(SuperExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitAwaitExpr(AwaitExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitArrayExpr(ArrayExpr expr) {
        return ANY;
    }

    @Override
    public TypeNode visitUnaryExprGeneric(UnaryExpr expr) {
        return ANY;
    }
}
