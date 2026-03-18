package aurora.intellij;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class AuroraSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("AURORA_KEYWORD",     DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey TYPE_KW =
            createTextAttributesKey("AURORA_TYPE_KW",     DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey MODIFIER =
            createTextAttributesKey("AURORA_MODIFIER",    DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("AURORA_IDENTIFIER",  DefaultLanguageHighlighterColors.IDENTIFIER);

    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("AURORA_NUMBER",      DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey STRING =
            createTextAttributesKey("AURORA_STRING",      DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey BOOLEAN_LIT =
            createTextAttributesKey("AURORA_BOOLEAN",     DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey NULL_LIT =
            createTextAttributesKey("AURORA_NULL",        DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("AURORA_LINE_COMMENT",  DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey DOC_COMMENT =
            createTextAttributesKey("AURORA_DOC_COMMENT",   DefaultLanguageHighlighterColors.DOC_COMMENT);

    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("AURORA_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);

    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("AURORA_OPERATOR",    DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey PAREN =
            createTextAttributesKey("AURORA_PAREN",       DefaultLanguageHighlighterColors.PARENTHESES);

    public static final TextAttributesKey BRACE =
            createTextAttributesKey("AURORA_BRACE",       DefaultLanguageHighlighterColors.BRACES);

    public static final TextAttributesKey BRACKET =
            createTextAttributesKey("AURORA_BRACKET",     DefaultLanguageHighlighterColors.BRACKETS);

    public static final TextAttributesKey COMMA =
            createTextAttributesKey("AURORA_COMMA",       DefaultLanguageHighlighterColors.COMMA);

    public static final TextAttributesKey SEMICOLON =
            createTextAttributesKey("AURORA_SEMICOLON",   DefaultLanguageHighlighterColors.SEMICOLON);

    public static final TextAttributesKey DOT =
            createTextAttributesKey("AURORA_DOT",         DefaultLanguageHighlighterColors.DOT);

    public static final TextAttributesKey BAD_CHAR =
            createTextAttributesKey("AURORA_BAD_CHAR",    HighlighterColors.BAD_CHARACTER);

    public static final TextAttributesKey GENERIC_PARAM =
            createTextAttributesKey("AURORA_GENERIC_PARAM", DefaultLanguageHighlighterColors.CLASS_REFERENCE);

    private static final TextAttributesKey[] EMPTY = TextAttributesKey.EMPTY_ARRAY;

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new AuroraHighlightingLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == AuroraTokenTypes.KEYWORD)      return keys(KEYWORD);
        if (tokenType == AuroraTokenTypes.TYPE_KW)      return keys(TYPE_KW);
        if (tokenType == AuroraTokenTypes.MODIFIER)     return keys(MODIFIER);
        if (tokenType == AuroraTokenTypes.IDENTIFIER)   return keys(IDENTIFIER);
        if (tokenType == AuroraTokenTypes.NUMBER)       return keys(NUMBER);
        if (tokenType == AuroraTokenTypes.STRING)       return keys(STRING);
        if (tokenType == AuroraTokenTypes.BOOLEAN_LIT)  return keys(BOOLEAN_LIT);
        if (tokenType == AuroraTokenTypes.NULL_LIT)     return keys(NULL_LIT);
        if (tokenType == AuroraTokenTypes.LINE_COMMENT) return keys(LINE_COMMENT);
        if (tokenType == AuroraTokenTypes.DOC_COMMENT)  return keys(DOC_COMMENT);
        if (tokenType == AuroraTokenTypes.BLOCK_COMMENT)return keys(BLOCK_COMMENT);
        if (tokenType == AuroraTokenTypes.OPERATOR)     return keys(OPERATOR);
        if (tokenType == AuroraTokenTypes.PAREN)        return keys(PAREN);
        if (tokenType == AuroraTokenTypes.BRACE)        return keys(BRACE);
        if (tokenType == AuroraTokenTypes.BRACKET)      return keys(BRACKET);
        if (tokenType == AuroraTokenTypes.COMMA)        return keys(COMMA);
        if (tokenType == AuroraTokenTypes.SEMICOLON)    return keys(SEMICOLON);
        if (tokenType == AuroraTokenTypes.DOT)          return keys(DOT);
        if (tokenType == AuroraTokenTypes.BAD_CHAR
                || tokenType == TokenType.BAD_CHARACTER)       return keys(BAD_CHAR);
        return EMPTY;
    }

    private static TextAttributesKey[] keys(TextAttributesKey key) {
        return new TextAttributesKey[]{key};
    }
}