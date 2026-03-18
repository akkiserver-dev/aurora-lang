package aurora.lsp;

import aurora.analyzer.ModuleResolver;
import aurora.analyzer.SymbolResolver;
import aurora.parser.SourceLocation;
import aurora.parser.tree.*;
import aurora.parser.tree.decls.*;
import aurora.parser.tree.expr.*;
import aurora.parser.tree.stmt.*;
import aurora.parser.tree.type.GenericParameter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A visitor that identifies and collects semantic tokens for syntax highlighting in the IDE.
 * Translates AST nodes into the specific token types and modifiers defined in the LSP legend.
 */
public class SemanticTokenVisitor implements NodeVisitor<Void> {

    // Valid legend types mapped in Server:
    public static final int TYPE_CLASS = 0;
    public static final int TYPE_TYPE = 1;
    public static final int TYPE_FUNCTION = 2;
    public static final int TYPE_VARIABLE = 3;
    public static final int TYPE_PARAMETER = 4;
    public static final int TYPE_PROPERTY = 5;

    public static final int MOD_DECLARATION = 1 << 0;
    public static final int MOD_DEFINITION = 1 << 1;
    public static final int MOD_READONLY = 1 << 2;
    public static final int MOD_STATIC = 1 << 3;

    private static class Token {
        int line, col, length, type, modifiers;

        Token(int line, int col, int length, int type, int modifiers) {
            this.line = line;
            this.col = col;
            this.length = length;
            this.type = type;
            this.modifiers = modifiers;
        }
    }

    private final List<Token> tokens = new ArrayList<>();
    private final Program program;
    private final ModuleResolver modules;

    private SemanticTokenVisitor(Program program, ModuleResolver modules) {
        this.program = program;
        this.modules = modules;
    }

    public static List<Integer> getTokens(Program program, ModuleResolver modules) {
        SemanticTokenVisitor visitor = new SemanticTokenVisitor(program, modules);
        visitor.visitProgram(program);
        return visitor.build();
    }

    private void addToken(SourceLocation loc, int type, int modifiers) {
        if (loc == null || loc.line() <= 0)
            return;
        int len = loc.endColumn() - loc.column();
        if (loc.line() != loc.endLine())
            len = loc.endOffset() - loc.startOffset();
        if (len <= 0)
            return;
        tokens.add(new Token(loc.line() - 1, loc.column(), len, type, modifiers));
    }

    private List<Integer> build() {
        tokens.sort(Comparator.comparingInt((Token t) -> t.line).thenComparingInt(t -> t.col));
        List<Integer> result = new ArrayList<>(tokens.size() * 5);
        int prevLine = 0, prevCol = 0;
        for (Token t : tokens) {
            int deltaLine = t.line - prevLine;
            int deltaCol = deltaLine == 0 ? t.col - prevCol : t.col;
            result.add(deltaLine);
            result.add(deltaCol);
            result.add(t.length);
            result.add(t.type);
            result.add(t.modifiers);
            prevLine = t.line;
            prevCol = t.col;
        }
        return result;
    }

    private void visitTypeParams(List<GenericParameter> typeParams) {
        if (typeParams == null) return;
        for (GenericParameter tp : typeParams) {
            if (tp.loc != null)
                addToken(tp.loc, TYPE_TYPE, MOD_DECLARATION);
            tp.constraints.forEach(this::visitTypeNode);
        }
    }

    // --- Core Routing ---
    @Override
    public Void visitNode(Node node) {
        return null;
    }

    @Override
    public Void visitProgram(Program program) {
        if (program.aPackage != null)
            visitProgramPackage(program.aPackage);
        if (program.imports != null)
            program.imports.forEach(this::visitProgramImport);
        if (program.statements != null)
            program.statements.forEach(this::visitStatement);
        return null;
    }

    @Override
    public Void visitProgramPackage(Program.Package pkg) {
        return null;
    }

    @Override
    public Void visitProgramImport(Program.Import imp) {
        return null;
    }

    @Override
    public Void visitProgramImportWildCard(Program.ImportWildCard imp) {
        return null;
    }

    @Override
    public Void visitProgramImportAlias(Program.ImportAlias imp) {
        return null;
    }

    @Override
    public Void visitProgramImportMulti(Program.ImportMulti imp) {
        return null;
    }

    @Override
    public Void visitTypeNodeLambda(TypeNode.Lambda lambda) {
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration decl) {
        // Dispatch to the correct specific visitor
        if (decl instanceof FieldDecl d)
            visitFieldDecl(d);
        else if (decl instanceof FunctionDecl d)
            visitFunctionDecl(d);
        else if (decl instanceof ClassDecl d)
            visitClassDecl(d);
        else if (decl instanceof InterfaceDecl d)
            visitInterfaceDecl(d);
        else if (decl instanceof RecordDecl d)
            visitRecordDecl(d);
        else if (decl instanceof EnumDecl d)
            visitEnumDecl(d);
        else if (decl instanceof ConstructorDecl d)
            visitConstructorDecl(d);
        else if (decl instanceof ParamDecl d)
            visitParamDecl(d);
        return null;
    }

    @Override
    public Void visitErrorNode(ErrorNode err) {
        return null;
    }

    @Override
    public Void visitStatement(Statement stmt) {
        if (stmt instanceof ExprStmt s)
            visitExprStmt(s);
        else if (stmt instanceof BlockStmt s)
            visitBlockStmt(s);
        else if (stmt instanceof IfStmt s)
            visitIfStmt(s);
        else if (stmt instanceof ControlStmt s)
            visitControlStmt(s);
        else if (stmt instanceof LoopStmt s)
            visitLoopStmt(s);
        else if (stmt instanceof MatchStmt s)
            visitMatchStmt(s);
        else if (stmt instanceof TryStmt s)
            visitTryStmt(s);
        else if (stmt instanceof LabeledStmt s)
            visitLabeledStmt(s);
        else if (stmt instanceof FieldDecl s)
            visitFieldDecl(s);
        else if (stmt instanceof FunctionDecl s)
            visitFunctionDecl(s);
        else if (stmt instanceof ClassDecl s)
            visitClassDecl(s);
        else if (stmt instanceof InterfaceDecl s)
            visitInterfaceDecl(s);
        else if (stmt instanceof RecordDecl s)
            visitRecordDecl(s);
        else if (stmt instanceof EnumDecl s)
            visitEnumDecl(s);
        else if (stmt instanceof ConstructorDecl s)
            visitConstructorDecl(s);
        return null;
    }

    // --- TYPES ---
    @Override
    public Void visitTypeNode(TypeNode type) {
        if (type.name != null && !type.name.isEmpty()) {
            // Highlight primitive types differently if desired, or just use TYPE_TYPE
            // Common primitives are often TYPE_TYPE or a specific predefined type.
            addToken(type.loc, TYPE_TYPE, 0);
        }
        if (type.typeArguments != null)
            type.typeArguments.forEach(this::visitTypeNode);
        return null;
    }

    // --- VARIABLES ---
    @Override
    public Void visitFieldDecl(FieldDecl decl) {
        int mods = MOD_DECLARATION;
        if (decl._static)
            mods |= MOD_STATIC;
        if (decl.declType == FieldDecl.Type.VAL)
            mods |= MOD_READONLY;

        if (decl.nameLoc != null)
            addToken(decl.nameLoc, TYPE_VARIABLE, mods);

        if (decl.type != null)
            visitTypeNode(decl.type);
        if (decl.init != null)
            visitExpr(decl.init);
        return null;
    }

    @Override
    public Void visitParamDecl(ParamDecl decl) {
        if (decl.nameLoc != null)
            addToken(decl.nameLoc, TYPE_PARAMETER, MOD_DECLARATION);

        if (decl.type != null)
            visitTypeNode(decl.type);
        if (decl.defaultValue != null)
            visitExpr(decl.defaultValue);
        return null;
    }

    // --- OTHER EXPRS ---
    @Override
    public Void visitExpr(Expr expr) {
        if (expr instanceof AccessExpr e)
            visitAccessExpr(e);
        else if (expr instanceof BinaryExpr e)
            visitBinaryExpr(e);
        else if (expr instanceof UnaryExpr e)
            visitUnaryExpr(e);
        else if (expr instanceof CallExpr e)
            visitCallExpr(e);
        else if (expr instanceof CastExpr e)
            visitCastExpr(e);
        else if (expr instanceof TypeCheckExpr e)
            visitTypeCheckExpr(e);
        else if (expr instanceof ArrayExpr e)
            visitArrayExpr(e);
        else if (expr instanceof ElvisExpr e)
            visitElvisExpr(e);
        else if (expr instanceof IndexExpr e)
            visitIndexExpr(e);
        else if (expr instanceof LambdaExpr e)
            visitLambdaExpr(e);
        else if (expr instanceof LiteralExpr e)
            visitLiteralExpr(e);
        return null;
    }

    @Override
    public Void visitAccessExpr(AccessExpr expr) {
        if (expr.object != null) {
            visitExpr(expr.object);
            if (expr.memberLoc != null) {
                addToken(expr.memberLoc, TYPE_PROPERTY, 0);
            }
        } else {
            Node resolved = SymbolResolver.resolveTypeName(program, expr.member, modules);
            if (resolved instanceof ClassDecl || resolved instanceof InterfaceDecl || resolved instanceof RecordDecl
                    || resolved instanceof EnumDecl || resolved instanceof Program.Import) {
                addToken(expr.loc, TYPE_CLASS, 0);
            } else if (resolved instanceof FunctionDecl) {
                addToken(expr.loc, TYPE_FUNCTION, 0);
            } else {
                addToken(expr.loc, TYPE_VARIABLE, 0);
            }
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        visitExpr(expr.left);
        visitExpr(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        visitExpr(expr.operand);
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr expr) {
        visitExpr(expr.callee);
        if (expr.arguments != null) {
            for (CallExpr.Argument arg : expr.arguments)
                visitExpr(arg.value);
        }
        return null;
    }

    @Override
    public Void visitCastExpr(CastExpr expr) {
        visitExpr(expr.expr);
        visitTypeNode(expr.type);
        return null;
    }

    @Override
    public Void visitTypeCheckExpr(TypeCheckExpr expr) {
        visitExpr(expr.check);
        visitTypeNode(expr.type);
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt stmt) {
        if (stmt.expr != null)
            visitExpr(stmt.expr);
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt stmt) {
        if (stmt.statements != null) {
            for (Statement s : stmt.statements)
                visitStatement(s);
        }
        return null;
    }

    // -- BOILERPLATE --
    @Override
    public Void visitIfStmt(IfStmt stmt) {
        visitExpr(stmt.condition);
        visitBlockStmt(stmt.thenBlock);
        if (stmt.elseIfs != null)
            stmt.elseIfs.forEach(this::visitIfStmtElseIf);
        if (stmt.elseBlock != null)
            visitBlockStmt(stmt.elseBlock);
        return null;
    }

    @Override
    public Void visitControlStmt(ControlStmt stmt) {
        if (stmt instanceof ControlStmt.ReturnStmt s)
            visitReturnStmt(s);
        else if (stmt instanceof ControlStmt.ThrowStmt s)
            visitThrowStmt(s);
        else if (stmt instanceof ControlStmt.BreakStmt s)
            visitBreakStmt(s);
        else if (stmt instanceof ControlStmt.ContinueStmt s)
            visitContinueStmt(s);
        return null;
    }

    @Override
    public Void visitLoopStmt(LoopStmt stmt) {
        if (stmt instanceof LoopStmt.WhileStmt s)
            visitWhileStmt(s);
        else if (stmt instanceof LoopStmt.RepeatUntilStmt s)
            visitRepeatUntilStmt(s);
        else if (stmt instanceof LoopStmt.ForStmt s)
            visitForStmt(s);
        return null;
    }

    @Override
    public Void visitIfStmtElseIf(IfStmt.ElseIf elseif) {
        visitExpr(elseif.condition);
        visitBlockStmt(elseif.block);
        return null;
    }

    @Override
    public Void visitWhileStmt(LoopStmt.WhileStmt stmt) {
        visitExpr(stmt.condition);
        visitBlockStmt(stmt.body);
        return null;
    }

    @Override
    public Void visitRepeatUntilStmt(LoopStmt.RepeatUntilStmt stmt) {
        visitBlockStmt(stmt.body);
        visitExpr(stmt.condition);
        return null;
    }

    @Override
    public Void visitForStmt(LoopStmt.ForStmt stmt) {
        visitExpr(stmt.iterable);
        visitBlockStmt(stmt.body);
        return null;
    }

    @Override
    public Void visitTryStmt(TryStmt stmt) {
        visitBlockStmt(stmt.tryBlock);
        if (stmt.catches != null)
            stmt.catches.forEach(this::visitTryStmtCatch);
        if (stmt.finallyBlock != null)
            visitBlockStmt(stmt.finallyBlock);
        return null;
    }

    @Override
    public Void visitTryStmtCatch(TryStmt.CatchClause catchClause) {
        visitBlockStmt(catchClause.block());
        return null;
    }

    @Override
    public Void visitMatchStmt(MatchStmt stmt) {
        visitExpr(stmt.expression);
        if (stmt.cases != null)
            stmt.cases.forEach(this::visitMatchCase);
        return null;
    }

    @Override
    public Void visitMatchCase(MatchStmt.MatchCase matchCase) {
        visitMatchPattern(matchCase.pattern);
        if (matchCase.guard != null)
            visitExpr(matchCase.guard);
        if (matchCase.body instanceof Expr e)
            visitExpr(e);
        else if (matchCase.body instanceof Statement s)
            visitStatement(s);
        return null;
    }

    @Override
    public Void visitMatchPattern(MatchStmt.Pattern pattern) {
        if (pattern instanceof MatchStmt.LiteralPattern p)
            visitLiteralPattern(p);
        else if (pattern instanceof MatchStmt.RangePattern p)
            visitRangePattern(p);
        else if (pattern instanceof MatchStmt.IsPattern p)
            visitIsPattern(p);
        else if (pattern instanceof MatchStmt.IdentifierPattern p)
            visitIdentifierPattern(p);
        else if (pattern instanceof MatchStmt.DestructurePattern p)
            visitDestructurePattern(p);
        else if (pattern instanceof MatchStmt.DefaultPattern p)
            visitDefaultPattern(p);
        return null;
    }

    @Override
    public Void visitLiteralPattern(MatchStmt.LiteralPattern pattern) {
        visitExpr(pattern.literal);
        return null;
    }

    @Override
    public Void visitRangePattern(MatchStmt.RangePattern pattern) {
        visitExpr(pattern.start);
        visitExpr(pattern.end);
        return null;
    }

    @Override
    public Void visitIsPattern(MatchStmt.IsPattern pattern) {
        visitTypeNode(pattern.type);
        return null;
    }

    @Override
    public Void visitIdentifierPattern(MatchStmt.IdentifierPattern pattern) {
        addToken(pattern.loc, TYPE_VARIABLE, MOD_DECLARATION);
        return null;
    }

    @Override
    public Void visitDestructurePattern(MatchStmt.DestructurePattern pattern) {
        return null;
    }

    @Override
    public Void visitDefaultPattern(MatchStmt.DefaultPattern pattern) {
        return null;
    }

    @Override
    public Void visitLabeledStmt(LabeledStmt stmt) {
        visitStatement(stmt.statement);
        return null;
    }

    @Override
    public Void visitInitializerBlock(InitializerBlock stmt) {
        visitBlockStmt(stmt.body);
        return null;
    }

    @Override
    public Void visitReturnStmt(ControlStmt.ReturnStmt stmt) {
        if (stmt.value != null)
            visitExpr(stmt.value);
        return null;
    }

    @Override
    public Void visitThrowStmt(ControlStmt.ThrowStmt stmt) {
        if (stmt.value != null)
            visitExpr(stmt.value);
        return null;
    }

    @Override
    public Void visitBreakStmt(ControlStmt.BreakStmt stmt) {
        return null;
    }

    @Override
    public Void visitContinueStmt(ControlStmt.ContinueStmt stmt) {
        return null;
    }

    @Override
    public Void visitClassDecl(ClassDecl decl) {
        if (decl.nameLoc != null)
            addToken(decl.nameLoc, TYPE_CLASS, MOD_DECLARATION);
        visitTypeParams(decl.typeParams);
        if (decl.superClass != null)
            visitTypeNode(decl.superClass);
        decl.interfaces.forEach(this::visitTypeNode);
        if (decl.members != null)
            decl.members.forEach(this::visitDeclaration);
        return null;
    }

    @Override
    public Void visitClassParamDecl(ClassParamDecl decl) {
        if (decl.type != null)
            visitTypeNode(decl.type);
        return null;
    }

    @Override
    public Void visitEnumDecl(EnumDecl decl) {
        if (decl.nameLoc != null)
            addToken(decl.nameLoc, TYPE_CLASS, MOD_DECLARATION);
        if (decl.members != null)
            decl.members.forEach(this::visitEnumMember);
        return null;
    }

    @Override
    public Void visitEnumMember(EnumDecl.EnumMember member) {
        return null;
    }

    @Override
    public Void visitConstructorDecl(ConstructorDecl decl) {
        if (decl.params != null)
            decl.params.forEach(this::visitParamDecl);
        visitBlockStmt(decl.body);
        return null;
    }

    @Override
    public Void visitFunctionDecl(FunctionDecl decl) {
        if (decl.nameLoc != null)
            addToken(decl.nameLoc, TYPE_FUNCTION, MOD_DECLARATION);
        visitTypeParams(decl.typeParams);
        if (decl.params != null)
            decl.params.forEach(this::visitParamDecl);
        if (decl.returnType != null)
            visitTypeNode(decl.returnType);
        if (decl.body != null)
            visitBlockStmt(decl.body);
        return null;
    }

    @Override
    public Void visitThreadDecl(ThreadDecl decl) {
        if (decl.body != null)
            visitBlockStmt(decl.body);
        return null;
    }

    @Override
    public Void visitRecordDecl(RecordDecl decl) {
        if (decl.nameLoc != null)
            addToken(decl.nameLoc, TYPE_CLASS, MOD_DECLARATION);
        visitTypeParams(decl.typeParams); // 追加
        if (decl.parameters != null)
            decl.parameters.forEach(this::visitClassParamDecl);
        decl.implementsInterfaces.forEach(this::visitTypeNode);
        if (decl.members != null)
            decl.members.forEach(this::visitDeclaration);
        return null;
    }

    @Override
    public Void visitMethodSignature(MethodSignature sig) {
        return null;
    }

    @Override
    public Void visitInterfaceDecl(InterfaceDecl decl) {
        if (decl.nameLoc != null)
            addToken(decl.nameLoc, TYPE_CLASS, MOD_DECLARATION);
        visitTypeParams(decl.typeParams);
        decl.interfaces.forEach(this::visitTypeNode);
        if (decl.members != null)
            decl.members.forEach(this::visitDeclaration);
        return null;
    }

    @Override
    public Void visitIfExpr(IfExpr expr) {
        visitExpr(expr.condition);
        visitBlockStmt(expr.thenBlock);
        if (expr.elseBlock != null)
            visitBlockStmt(expr.elseBlock);
        return null;
    }

    @Override
    public Void visitMatchExpr(MatchExpr expr) {
        visitExpr(expr.expression);
        return null;
    }

    @Override
    public Void visitRangeExpr(RangeExpr expr) {
        visitExpr(expr.start);
        visitExpr(expr.end);
        return null;
    }

    @Override
    public Void visitThreadExpr(ThreadExpr expr) {
        if (expr.body != null)
            visitBlockStmt(expr.body);
        return null;
    }

    @Override
    public Void visitSelfExpr(SelfExpr expr) {
        return null;
    }

    @Override
    public Void visitSuperExpr(SuperExpr expr) {
        return null;
    }

    @Override
    public Void visitAwaitExpr(AwaitExpr expr) {
        visitExpr(expr.expr);
        return null;
    }

    @Override
    public Void visitUnaryExprGeneric(UnaryExpr expr) {
        visitExpr(expr.operand);
        return null;
    }

    @Override
    public Void visitArrayExpr(ArrayExpr expr) {
        if (expr.elements != null)
            expr.elements.forEach(this::visitExpr);
        return null;
    }

    @Override
    public Void visitElvisExpr(ElvisExpr expr) {
        visitExpr(expr.left);
        visitExpr(expr.right);
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr expr) {
        return null;
    }

    @Override
    public Void visitLambdaExpr(LambdaExpr expr) {
        if (expr.body instanceof Expr e)
            visitExpr(e);
        else if (expr.body instanceof Statement s)
            visitStatement(s);
        return null;
    }

    @Override
    public Void visitIndexExpr(IndexExpr expr) {
        visitExpr(expr.object);
        visitExpr(expr.index);
        return null;
    }
}