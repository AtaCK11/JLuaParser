package main.lexer;

import main.util.Span;

/**
 * @param lexeme substring of source
 */
public record Token(TokenType type, String lexeme, Span span) {

    @Override
    public String toString() {
        return type + "('" + lexeme + "')@" +
                span.getStart().getLine() + ":" + span.getStart().getColumn();
    }
}