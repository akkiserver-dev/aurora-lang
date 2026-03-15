package aurora.intellij;

import aurora.compiler.antlr.AuroraLexer;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AuroraHighlightingLexer extends LexerBase {

    private CharSequence buffer;
    private int endOffset;

    private final List<Object[]> tokens = new ArrayList<>();
    private int currentIndex;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.endOffset = endOffset;
        this.tokens.clear();
        this.currentIndex = 0;

        String text = buffer.subSequence(startOffset, endOffset).toString();

        AuroraLexer lexer = new AuroraLexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();

        List<Token> antlrTokens = new ArrayList<>();
        Token t;
        while ((t = lexer.nextToken()).getType() != Token.EOF) {
            antlrTokens.add(t);
        }

        int pos = 0;
        for (Token token : antlrTokens) {
            int tStart = token.getStartIndex();
            int tEnd   = token.getStopIndex() + 1;

            if (tStart > pos) {
                fillGap(text, startOffset, pos, tStart);
            }

            IElementType type = AuroraTokenTypes.fromAntlrType(token.getType());
            addToken(startOffset + tStart, startOffset + tEnd, type);
            pos = tEnd;
        }

        if (pos < text.length()) {
            fillGap(text, startOffset, pos, text.length());
        }
    }

    private void fillGap(String text, int baseOffset, int start, int end) {
        int i = start;
        while (i < end) {
            char c = text.charAt(i);
            if (i + 1 < end && c == '/' && text.charAt(i + 1) == '/') {
                int lineEnd = i;
                while (lineEnd < end && text.charAt(lineEnd) != '\n' && text.charAt(lineEnd) != '\r') {
                    lineEnd++;
                }
                addToken(baseOffset + i, baseOffset + lineEnd, AuroraTokenTypes.LINE_COMMENT);
                i = lineEnd;
            } else if (i + 1 < end && c == '/' && text.charAt(i + 1) == '*') {
                boolean isDoc = (i + 2 < end && text.charAt(i + 2) == '*'
                        && (i + 3 >= end || text.charAt(i + 3) != '/'));
                int closeIdx = text.indexOf("*/", i + 2);
                int commentEnd = (closeIdx == -1) ? end : closeIdx + 2;
                IElementType type = isDoc ? AuroraTokenTypes.DOC_COMMENT : AuroraTokenTypes.BLOCK_COMMENT;
                addToken(baseOffset + i, baseOffset + commentEnd, type);
                i = commentEnd;
            } else if (isWhitespace(c)) {
                int wsEnd = i;
                while (wsEnd < end && isWhitespace(text.charAt(wsEnd))) {
                    wsEnd++;
                }
                addToken(baseOffset + i, baseOffset + wsEnd, AuroraTokenTypes.WHITE_SPACE);
                i = wsEnd;
            } else {
                addToken(baseOffset + i, baseOffset + i + 1, AuroraTokenTypes.BAD_CHAR);
                i++;
            }
        }
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n'
                || c == '\f'
                || c == '\u00A0'
                || c == '\u200B'
                || Character.isWhitespace(c);
    }

    private void addToken(int start, int end, IElementType type) {
        tokens.add(new Object[]{start, end, type});
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public @Nullable IElementType getTokenType() {
        if (currentIndex >= tokens.size()) return null;
        return (IElementType) tokens.get(currentIndex)[2];
    }

    @Override
    public int getTokenStart() {
        return currentIndex < tokens.size() ? (int) tokens.get(currentIndex)[0] : endOffset;
    }

    @Override
    public int getTokenEnd() {
        return currentIndex < tokens.size() ? (int) tokens.get(currentIndex)[1] : endOffset;
    }

    @Override
    public void advance() {
        currentIndex++;
    }

    @Override
    public @NotNull CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}