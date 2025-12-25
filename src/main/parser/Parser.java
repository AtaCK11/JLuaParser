package main.parser;

import main.ast.*;
import main.ast.exp.Expression;
import main.ast.stmt.*;
import main.lexer.Lexer;
import main.lexer.Token;
import main.lexer.TokenType;
import main.parser.exceptions.LuaRuleException;
import main.parser.exceptions.ParseException;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    public final List<Token> tokens;
    public int current = 0;

    public final ExpressionParser expr;
    public final StatementParser stmt;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.expr = new ExpressionParser(this);
        this.stmt = new StatementParser(this);
    }

    public Parser(String source) {
        this.tokens = new Lexer(source).tokenize();
        this.expr = new ExpressionParser(this);
        this.stmt = new StatementParser(this);
    }

    // =======================================================
    // ENTRY: parse a full chunk
    // =======================================================

    public Chunk parseChunk() {
        List<Statement> statements = new ArrayList<>();

        while (!isBlockEnd(peek())) {
            Statement parseStatement = stmt.parseStatement();
            statements.add(parseStatement);

            match(TokenType.SEMICOLON); // Optional semicolon after statement

            // LUA RULE: return (and break) must be the last statement in a block
            if (parseStatement instanceof ReturnStatement || parseStatement instanceof BreakStatement || parseStatement instanceof ContinueStatement) {

                // consume optional trailing semicolon
                match(TokenType.SEMICOLON);

                if (!isBlockEnd(peek())) {
                    throw luaRuleError(peek(), "no statement allowed after 'return', 'break' or 'continue' in the same block");
                }

                break;
            }

        }

        Token first = tokens.getFirst();
        Token last  = previous();

        Span span = new Span(
                first.span().getStartOffset(),
                last.span().getEndOffset(),
                first.span().getStart(),
                last.span().getEnd()
        );

        return new Chunk(new Block(statements, span, List.of(), List.of()), span, List.of(), List.of());
    }

    // =======================================================
    // Shared token utilities
    // =======================================================

    public boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) {
                advance();
                return true;
            }
        }
        return false;
    }

    public boolean check(TokenType type) {
        return peek().type() == type;
    }

    public Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    public boolean isAtEnd() {
        // EOF or END token
        return peek().type() == TokenType.EOF;
    }

    public boolean isReturnTerminator() {
        return check(TokenType.SEMICOLON)
                || check(TokenType.END)
                || check(TokenType.ELSE)
                || check(TokenType.ELSEIF)
                || check(TokenType.UNTIL)
                || isAtEnd();
    }

    public boolean isBlockEnd(Token t) {
        return t.type() == TokenType.END ||
                t.type() == TokenType.ELSE ||
                t.type() == TokenType.ELSEIF ||
                t.type() == TokenType.UNTIL ||
                t.type() == TokenType.EOF;
    }

    public boolean isLookaheadOnNewLine() {
        if (isAtEnd()) return false;
        Token previous = previous();
        Token next = peek();
        return next.span().getStart().getLine()> previous.span().getStart().getLine();
    }

    public Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    public Token peek() {
        return tokens.get(current);
    }

    public Token peekNext() {
        if (current + 1 >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(current + 1);
    }

    public Token previous() {
        return tokens.get(current - 1);
    }

    public ParseException error(Token tok, String msg) {
        int line = tok.span().getStart().getLine();
        return new ParseException("[line " + line + "] " + msg + " token: " + tok.type());
    }

    public LuaRuleException luaRuleError(Token tok, String msg) {
        int line = tok.span().getStart().getLine();
        return new LuaRuleException("[line " + line + "] " + msg + " token: " + tok.type());
    }

    // handy span helpers
    Span combine(Node a, Node b) {
        return new Span(
                a.getSpan().getStartOffset(),
                b.getSpan().getEndOffset(),
                a.getSpan().getStart(),
                b.getSpan().getEnd()
        );
    }

    Span combine(Token a, Token b) {
        return new Span(
                a.span().getStartOffset(),
                b.span().getEndOffset(),
                a.span().getStart(),
                b.span().getEnd()
        );
    }

    public Span combine(Expression nameExpr, Token field) {
        return new Span(
                nameExpr.getSpan().getStartOffset(),
                field.span().getEndOffset(),
                nameExpr.getSpan().getStart(),
                field.span().getEnd()
        );
    }
}