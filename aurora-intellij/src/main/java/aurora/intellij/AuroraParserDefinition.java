package aurora.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class AuroraParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType("AURORA_FILE", AuroraLang.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new AuroraHighlightingLexer();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return (root, builder) -> {
            com.intellij.lang.PsiBuilder.Marker mark = builder.mark();
            while (!builder.eof()) {
                builder.advanceLexer();
            }
            mark.done(root);
            return builder.getTreeBuilt();
        };
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.create(AuroraTokenTypes.WHITE_SPACE);
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(
                AuroraTokenTypes.LINE_COMMENT,
                AuroraTokenTypes.DOC_COMMENT,
                AuroraTokenTypes.BLOCK_COMMENT);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.create(AuroraTokenTypes.STRING);
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        throw new UnsupportedOperationException("Aurora PSI elements are not implemented: " + node.getElementType());
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new AuroraFile(viewProvider);
    }

    public static class AuroraFile extends PsiFileImpl {
        public AuroraFile(@NotNull FileViewProvider viewProvider) {
            super(FILE, FILE, viewProvider);
        }

        @Override
        public @NotNull AuroraLangFileType getFileType() {
            return AuroraLangFileType.INSTANCE;
        }

        @Override
        public void accept(@NotNull PsiElementVisitor psiElementVisitor) {

        }

        @Override
        public String toString() {
            return "AuroraFile: " + getName();
        }
    }
}