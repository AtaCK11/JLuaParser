package main.lexer;

import main.util.Position;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {

    private final char[] input;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.input = source != null ? source.toCharArray() : new char[0];
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = nextToken();
            tokens.add(t);
        } while (t.type() != TokenType.EOF);
        return tokens;
    }

    private Token nextToken() {
        skipWhitespaceAndComments();

        if (isAtEnd()) {
            Span span = makeSpan(index, index, new Position(line, column));
            return new Token(TokenType.EOF, "", span);
        }

        int startIndex = index;
        Position startPos = new Position(line, column);
        char c = advance();

        // Identifiers / keywords
        if (isAlpha(c) || c == '_') {
            while (!isAtEnd() && (isAlphaNumeric(peek()) || peek() == '_')) {
                advance();
            }
            String text = new String(input, startIndex, index - startIndex);
            TokenType type = TokenType.classifyIdentifier(text);
            Span span = makeSpan(startIndex, index, startPos);
            return new Token(type, text, span);
        }

        // Numbers (basic decimal/float; extend later for hex/exponents)
        if (isDigit(c)) {
            boolean hasDot = false;
            while (!isAtEnd()) {
                char p = peek();
                if (isDigit(p)) {
                    advance();
                } else if (p == '.' && !hasDot && isDigit(peekNext())) {
                    hasDot = true;
                    advance(); // consume '.'
                } else {
                    break;
                }
            }
            String text = new String(input, startIndex, index - startIndex);
            Span span = makeSpan(startIndex, index, startPos);
            return new Token(TokenType.NUMBER, text, span);
        }

        // String literals: '...' or "..." (simplified)
        if (c == '"' || c == '\'') {
            char quote = c;
            while (!isAtEnd()) {
                char ch = advance();
                if (ch == quote) {
                    break;
                }
                if (ch == '\\' && !isAtEnd()) {
                    advance(); // consume escaped char, but don't interpret yet
                }
            }
            String text = new String(input, startIndex, index - startIndex);
            Span span = makeSpan(startIndex, index, startPos);
            return new Token(TokenType.STRING, text, span);
        }

        // Operators
        TokenType type;
        switch (c) {
            case '+':
                //if (match('+'))      type = TokenType.INCREMENT;
                if (match('=')) type = TokenType.PLUS_EQUAL;
                else                          type = TokenType.PLUS;
                break;

            // --, -=, -
            case '-':
                //if (match('-'))      type = TokenType.DECREMENT;
                if (match('='))      type = TokenType.MINUS_EQUAL;
                else                          type = TokenType.MINUS;
                break;

            // *=, *
            case '*':
                if (match('=')) type = TokenType.STAR_EQUAL;
                else                     type = TokenType.STAR;
                break;

            // /=, /
            case '/':
                if (match('/')) type = TokenType.FLOOR_DIV;
                if (match('=')) type = TokenType.SLASH_EQUAL;
                else                     type = TokenType.SLASH;
                break;

            // %=, %
            case '%':
                if (match('=')) type = TokenType.PERCENT_EQUAL;
                else                     type = TokenType.PERCENT;
                break;
            case '^': type = TokenType.CARET; break;
            case '#': type = TokenType.HASH; break;
            case '&': type = TokenType.BITWISE_AND; break;
            case '|': type = TokenType.BITWISE_OR; break;

            case '(': type = TokenType.LPAREN; break;
            case ')': type = TokenType.RPAREN; break;
            case '{': type = TokenType.LBRACE; break;
            case '}': type = TokenType.RBRACE; break;
            case '[': type = TokenType.LBRACKET; break;
            case ']': type = TokenType.RBRACKET; break;

            case ';': type = TokenType.SEMICOLON; break;
            case ',': type = TokenType.COMMA; break;
            case ':': type = TokenType.COLON; break;

            case '.':
                if (match('.')) {
                    if (match('.')) {
                        type = TokenType.VARARG;
                    } else {
                        type = TokenType.DOT_DOT;
                    }
                } else {
                    type = TokenType.DOT;
                }
                break;

            case '=':
                if (match('=')) type = TokenType.EQUAL;
                else type = TokenType.ASSIGN;
                break;

            case '~':
                if (match('=')) type = TokenType.NOT_EQUAL;
                else type = TokenType.BITWISE_XOR;
                break;

            case '<':
                if (match('<')) type = TokenType.BITWISE_SHL;
                if (match('=')) type = TokenType.LESS_EQUAL;
                else type = TokenType.LESS;
                break;

            case '>':
                if (match('>')) type = TokenType.BITWISE_SHR;
                if (match('=')) type = TokenType.GREATER_EQUAL;
                else type = TokenType.GREATER;
                break;



            default:
                type = TokenType.UNKNOWN;
                break;
        }

        String text = new String(input, startIndex, index - startIndex);
        Span span = makeSpan(startIndex, index, startPos);
        return new Token(type, text, span);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void skipWhitespaceAndComments() {
        boolean again;
        do {
            again = false;

            // whitespace
            while (!isAtEnd() && Character.isWhitespace(peek())) {
                advance();
            }

            // comments: "-- ..." or "--[[ ... ]]"
            if (!isAtEnd() && peek() == '-' && peekNext() == '-') {
                advance(); // '-'
                advance(); // '-'

                // long comment: --[[ ... ]]
                if (!isAtEnd() && peek() == '[' && peekNext() == '[') {
                    advance(); // '['
                    advance(); // '['
                    while (!isAtEnd()) {
                        if (peek() == ']' && peekNext() == ']') {
                            advance();
                            advance();
                            break;
                        }
                        advance();
                    }
                } else {
                    // line comment
                    while (!isAtEnd() && peek() != '\n') {
                        advance();
                    }
                }
                again = true;
            }
        } while (again);
    }

    private boolean isAtEnd() {
        return index >= input.length;
    }

    private char peek() {
        return isAtEnd() ? '\0' : input[index];
    }

    private char peekNext() {
        return (index + 1 >= input.length) ? '\0' : input[index + 1];
    }

    private char advance() {
        char c = input[index++];
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (input[index] != expected) return false;
        advance();
        return true;
    }

    private static boolean isAlpha(char c) {
        return Character.isLetter(c);
    }

    private static boolean isAlphaNumeric(char c) {
        return Character.isLetterOrDigit(c);
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private Span makeSpan(int startIndex, int endIndex, Position startPos) {
        Position endPos = new Position(line, column);
        return new Span(startIndex, endIndex, startPos, endPos);
    }
}
