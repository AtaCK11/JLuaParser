package main.lexer;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
    // Special
    EOF(null, null),
    IDENTIFIER(null, null),
    NUMBER(null, null),
    STRING(null, null),

    // Keywords (Lua 5.x core)
    LOGICAL_AND("and", null),
    BREAK("break", null),
    DO("do", null),
    ELSE("else", null),
    ELSEIF("elseif", null),
    END("end", null),
    FALSE("false", null),
    FOR("for", null),
    FUNCTION("function", null),
    GOTO("goto", null),
    IF("if", null),
    IN("in", null),
    LOCAL("local", null),
    NIL("nil", null),
    NOT("not", null),
    LOGICAL_OR("or", null),
    REPEAT("repeat", null),
    RETURN("return", null),
    THEN("then", null),
    TRUE("true", null),
    UNTIL("until", null),
    WHILE("while", null),
    CONTINUE("continue", null),

    // Symbols / operators – text is for reference/pretty-printing
    PLUS(null, "+"),
    MINUS(null, "-"),
    STAR(null, "*"),
    SLASH(null, "/"),
    PERCENT(null, "%"),
    CARET(null, "^"),
    HASH(null, "#"),
    BITWISE_AND(null, "&"),
    BITWISE_OR(null, "|"),
    BITWISE_XOR(null, "~"),
    BITWISE_SHL(null, "<<"),
    BITWISE_SHR(null, ">>"),
    FLOOR_DIV(null, "//"),

    LPAREN(null, "("),
    RPAREN(null, ")"),
    LBRACE(null, "{"),
    RBRACE(null, "}"),
    LBRACKET(null, "["),
    RBRACKET(null, "]"),

    SEMICOLON(null, ";"),
    COLON(null, ":"),
    COMMA(null, ","),

    DOT(null, "."),
    DOT_DOT(null, ".."),
    VARARG(null, "..."),

    ASSIGN(null, "="),
    EQUAL(null, "=="),
    NOT_EQUAL(null, "~="),
    LESS(null, "<"),
    LESS_EQUAL(null, "<="),
    GREATER(null, ">"),
    GREATER_EQUAL(null, ">="),

    // Luau compound operators
    PLUS_EQUAL(null, "+="),
    MINUS_EQUAL(null, "-="),
    STAR_EQUAL(null, "*="),
    SLASH_EQUAL(null, "/="),
    PERCENT_EQUAL(null, "%="),

    // Custom
    INCREMENT(null, "++"),
    DECREMENT(null, "--"),


    UNKNOWN(null, null);

    /** If non-null, this token represents a keyword with this exact text. */
    private final String keywordText;

    /** If non-null, this token represents a fixed symbol/operator with this text. */
    private final String symbolText;

    TokenType(String keywordText, String symbolText) {
        this.keywordText = keywordText;
        this.symbolText = symbolText;
    }

    public String getKeywordText() {
        return keywordText;
    }

    public String getSymbolText() {
        return symbolText;
    }

    public boolean isKeyword() {
        return keywordText != null;
    }

    public boolean isSymbol() {
        return symbolText != null;
    }

    // ---------------------------------------------------------------------
    // Keyword lookup table: used by Lexer, but defined here (data, not logic)
    // ---------------------------------------------------------------------

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        for (TokenType t : values()) {
            if (t.keywordText != null) {
                KEYWORDS.put(t.keywordText, t);
            }
        }
    }

    /**
     * If the given identifier text is a keyword, returns that TokenType,
     * otherwise returns IDENTIFIER.
     */
    public static TokenType classifyIdentifier(String text) {
        TokenType kw = KEYWORDS.get(text);
        return kw != null ? kw : IDENTIFIER;
    }

    @Override
    public String toString() {
        if (isKeyword()) {
            return "Keyword(" + keywordText + ")";
        } else if (isSymbol()) {
            return "Symbol(" + symbolText + ")";
        } else {
            return name();
        }
    }
}
