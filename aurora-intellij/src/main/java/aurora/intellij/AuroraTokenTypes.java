package aurora.intellij;

import aurora.compiler.antlr.AuroraLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.HashMap;
import java.util.Map;

public final class AuroraTokenTypes {
    public static final IElementType KEYWORD     = new IElementType("KEYWORD",      AuroraLang.INSTANCE);
    public static final IElementType TYPE_KW     = new IElementType("TYPE_KW",      AuroraLang.INSTANCE);
    public static final IElementType MODIFIER    = new IElementType("MODIFIER",     AuroraLang.INSTANCE);
    public static final IElementType IDENTIFIER  = new IElementType("IDENTIFIER",   AuroraLang.INSTANCE);
    public static final IElementType NUMBER      = new IElementType("NUMBER",        AuroraLang.INSTANCE);
    public static final IElementType STRING      = new IElementType("STRING",        AuroraLang.INSTANCE);
    public static final IElementType BOOLEAN_LIT = new IElementType("BOOLEAN_LIT",  AuroraLang.INSTANCE);
    public static final IElementType NULL_LIT    = new IElementType("NULL_LIT",     AuroraLang.INSTANCE);
    public static final IElementType LINE_COMMENT= new IElementType("LINE_COMMENT", AuroraLang.INSTANCE);
    public static final IElementType DOC_COMMENT = new IElementType("DOC_COMMENT",  AuroraLang.INSTANCE);
    public static final IElementType BLOCK_COMMENT = new IElementType("BLOCK_COMMENT", AuroraLang.INSTANCE);
    public static final IElementType OPERATOR    = new IElementType("OPERATOR",     AuroraLang.INSTANCE);
    public static final IElementType PAREN       = new IElementType("PAREN",        AuroraLang.INSTANCE);
    public static final IElementType BRACE       = new IElementType("BRACE",        AuroraLang.INSTANCE);
    public static final IElementType BRACKET     = new IElementType("BRACKET",      AuroraLang.INSTANCE);
    public static final IElementType COMMA       = new IElementType("COMMA",        AuroraLang.INSTANCE);
    public static final IElementType SEMICOLON   = new IElementType("SEMICOLON",    AuroraLang.INSTANCE);
    public static final IElementType DOT         = new IElementType("DOT",          AuroraLang.INSTANCE);
    public static final IElementType BAD_CHAR    = TokenType.BAD_CHARACTER;
    public static final IElementType WHITE_SPACE = TokenType.WHITE_SPACE;

    private static final Map<Integer, IElementType> TOKEN_MAP = new HashMap<>();

    static {
        for (int t : new int[]{
                AuroraLexer.ASYNC, AuroraLexer.AWAIT, AuroraLexer.BREAK, AuroraLexer.CATCH,
                AuroraLexer.CONTINUE, AuroraLexer.DEFAULT, AuroraLexer.DO, AuroraLexer.ELSE,
                AuroraLexer.ELSEIF, AuroraLexer.IF, AuroraLexer.FOR, AuroraLexer.MATCH,
                AuroraLexer.REPEAT, AuroraLexer.RETURN, AuroraLexer.THROW, AuroraLexer.TRY,
                AuroraLexer.UNTIL, AuroraLexer.WHILE, AuroraLexer.NONNULL
        }) TOKEN_MAP.put(t, KEYWORD);

        for (int t : new int[]{
                AuroraLexer.CLASS, AuroraLexer.CONSTRUCTOR, AuroraLexer.ENUM, AuroraLexer.OVERRIDE,
                AuroraLexer.RECORD, AuroraLexer.STATIC, AuroraLexer.SUPER, AuroraLexer.TRAIT,
                AuroraLexer.TYPE, AuroraLexer.NATIVE, AuroraLexer.ABSTRACT, AuroraLexer.FUN,
                AuroraLexer.IN, AuroraLexer.OUT, AuroraLexer.SELF, AuroraLexer.THREAD,
                AuroraLexer.USING, AuroraLexer.PACKAGE, AuroraLexer.FINALLY, AuroraLexer.AS,
                AuroraLexer.IS
        }) TOKEN_MAP.put(t, KEYWORD);

        for (int t : new int[]{
                AuroraLexer.BOOL, AuroraLexer.DOUBLE, AuroraLexer.FLOAT, AuroraLexer.INT,
                AuroraLexer.LONG, AuroraLexer.NONE, AuroraLexer.OBJECT, AuroraLexer.STRING,
                AuroraLexer.VOID, AuroraLexer.VARARGS
        }) TOKEN_MAP.put(t, TYPE_KW);

        for (int t : new int[]{
                AuroraLexer.LOCAL, AuroraLexer.PROTECTED, AuroraLexer.PUB,
                AuroraLexer.VAL, AuroraLexer.VAR
        }) TOKEN_MAP.put(t, MODIFIER);

        TOKEN_MAP.put(AuroraLexer.INTEGER_LITERAL, NUMBER);
        TOKEN_MAP.put(AuroraLexer.LONG_LITERAL,    NUMBER);
        TOKEN_MAP.put(AuroraLexer.DOUBLE_LITERAL,  NUMBER);
        TOKEN_MAP.put(AuroraLexer.FLOAT_LITERAL,   NUMBER);
        TOKEN_MAP.put(AuroraLexer.STRING_LITERAL,  STRING);
        TOKEN_MAP.put(AuroraLexer.TRUE,            BOOLEAN_LIT);
        TOKEN_MAP.put(AuroraLexer.FALSE,           BOOLEAN_LIT);
        TOKEN_MAP.put(AuroraLexer.NULL,            NULL_LIT);

        TOKEN_MAP.put(AuroraLexer.IDENTIFIER, IDENTIFIER);

        TOKEN_MAP.put(AuroraLexer.LINE_COMMENT, LINE_COMMENT);
        TOKEN_MAP.put(AuroraLexer.DOC_COMMENT,  DOC_COMMENT);

        for (int t : new int[]{
                AuroraLexer.QUESTION_DOT, AuroraLexer.BANG_BANG, AuroraLexer.COLON_COLON,
                AuroraLexer.RANGE_EXCL, AuroraLexer.RANGE_INCL,
                AuroraLexer.PLUS, AuroraLexer.MINUS, AuroraLexer.STAR, AuroraLexer.SLASH, AuroraLexer.PERCENT,
                AuroraLexer.EQ, AuroraLexer.NEQ, AuroraLexer.LT, AuroraLexer.GT, AuroraLexer.LE, AuroraLexer.GE,
                AuroraLexer.AND, AuroraLexer.OR, AuroraLexer.NOT,
                AuroraLexer.QUESTION, AuroraLexer.ELVIS,
                AuroraLexer.ASSIGN, AuroraLexer.PLUS_ASSIGN, AuroraLexer.MINUS_ASSIGN,
                AuroraLexer.STAR_ASSIGN, AuroraLexer.SLASH_ASSIGN, AuroraLexer.PERCENT_ASSIGN,
                AuroraLexer.ARROW, AuroraLexer.FAT_ARROW, AuroraLexer.COLON
        }) TOKEN_MAP.put(t, OPERATOR);

        TOKEN_MAP.put(AuroraLexer.LPAREN,    PAREN);
        TOKEN_MAP.put(AuroraLexer.RPAREN,    PAREN);
        TOKEN_MAP.put(AuroraLexer.LBRACE,    BRACE);
        TOKEN_MAP.put(AuroraLexer.RBRACE,    BRACE);
        TOKEN_MAP.put(AuroraLexer.LBRACK,    BRACKET);
        TOKEN_MAP.put(AuroraLexer.RBRACK,    BRACKET);
        TOKEN_MAP.put(AuroraLexer.COMMA,     COMMA);
        TOKEN_MAP.put(AuroraLexer.SEMICOLON, SEMICOLON);
        TOKEN_MAP.put(AuroraLexer.DOT,       DOT);
    }

    public static IElementType fromAntlrType(int antlrType) {
        return TOKEN_MAP.getOrDefault(antlrType, BAD_CHAR);
    }

    private AuroraTokenTypes() {}
}