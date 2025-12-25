package main.parser;

import main.ast.*;
import main.ast.exp.*;

import main.ast.stmt.Block;
import main.ast.stmt.Statement;
import main.lexer.Token;
import main.lexer.TokenType;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class ExpressionParser {

    private final Parser p;

    public ExpressionParser(Parser parser) {
        this.p = parser;
    }

    // =======================================================
    // Expression list: expr (',' expr)*
    // =======================================================

    public List<Expression> parseExpressionList() {
        List<Expression> list = new ArrayList<>();
        list.add(parseExpression());
        while (p.match(TokenType.COMMA)) {
            list.add(parseExpression());
        }
        return list;
    }

    // =======================================================
    // Precedence chain
    // =======================================================

    // 1. Existing parseExpression calls parseOr
    public Expression parseExpression() {
        return parseOr();
    }

    // 2. Logical OR (Lowest)
    private Expression parseOr() {
        Expression expr = parseAnd();
        while (p.match(TokenType.LOGICAL_OR)) {
            Token op = p.previous();
            Expression right = parseAnd();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 3. Logical AND
    private Expression parseAnd() {
        Expression expr = parseCompare();
        while (p.match(TokenType.LOGICAL_AND)) {
            Token op = p.previous();
            Expression right = parseCompare();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 4. Comparison
    private Expression parseCompare() {
        Expression expr = parseBitwiseOr();
        while (p.match(TokenType.EQUAL, TokenType.NOT_EQUAL, TokenType.LESS,
                TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token op = p.previous();
            Expression right = parseBitwiseOr();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 5. Bitwise OR (|)
    private Expression parseBitwiseOr() {
        Expression expr = parseBitwiseXor();
        while (p.match(TokenType.BITWISE_OR)) {
            Token op = p.previous();
            Expression right = parseBitwiseXor();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 6. Bitwise XOR (~)
    private Expression parseBitwiseXor() {
        Expression expr = parseBitwiseAnd();
        while (p.match(TokenType.BITWISE_XOR)) {
            Token op = p.previous();
            Expression right = parseBitwiseAnd();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 7. Bitwise AND (&)
    private Expression parseBitwiseAnd() {
        Expression expr = parseBitwiseShift();
        while (p.match(TokenType.BITWISE_AND)) {
            Token op = p.previous();
            Expression right = parseBitwiseShift();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 8. Bitwise Shift (<<, >>)
    private Expression parseBitwiseShift() {
        Expression expr = parseConcat();
        while (p.match(TokenType.BITWISE_SHL, TokenType.BITWISE_SHR)) {
            Token op = p.previous();
            Expression right = parseConcat();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 9. Concatenation (..) - Right-associative in Lua
    private Expression parseConcat() {
        Expression expr = parseAdd();
        if (p.match(TokenType.DOT_DOT)) {
            Token op = p.previous();
            Expression right = parseConcat(); // Recursive call for right-associativity
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 10. Addition and Subtraction
    private Expression parseAdd() {
        Expression expr = parseMul();
        while (p.match(TokenType.PLUS, TokenType.MINUS)) {
            Token op = p.previous();
            Expression right = parseMul();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    // 11. Multiplication, Division, Floor Div, Mod
    private Expression parseMul() {
        Expression expr = parseUnary();
        while (p.match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT, TokenType.FLOOR_DIV)) {
            Token op = p.previous();
            Expression right = parseUnary();
            expr = makeBinary(expr, op, right);
        }
        return expr;
    }

    private Expression parseUnary() {
        // 1. Check for unary operators (#, -, not)
        if (p.match(TokenType.NOT, TokenType.MINUS, TokenType.HASH)) {
            Token op = p.previous();
            // Unary operators call parseUnary again (right-associative)
            Expression right = parseUnary();

            Span span = new Span(
                    op.span().getStartOffset(),
                    right.getSpan().getEndOffset(),
                    op.span().getStart(),
                    right.getSpan().getEnd()
            );
            return new UnaryExpression(op.type(), right, span, List.of(), List.of());
        }
        // 2. If no unary operator, move to Power
        return parsePower();
    }

    private Expression parsePower() {
        // Left side is a primary expression (like '1')
        Expression expr = parsePrefixExpression();

        // Exponentiation (^) is right-associative
        if (p.match(TokenType.CARET)) {
            Token op = p.previous();

            // FIX: Call parseUnary here instead of parsePower or parsePrimary.
            // This allows the right side to be a unary expression like '#pi'
            Expression right = parseUnary();

            expr = makeBinary(expr, op, right);
        }

        return expr;
    }

    // =======================================================
    // Primary + prefix expressions (function calls)
    // =======================================================

    private Expression parsePrimary() {

        if (p.match(TokenType.FUNCTION)) {
            return parseAnonymousFunction();
        }

        if (p.match(TokenType.NUMBER)) {
            Token t = p.previous();
            return new LiteralExpression(t.lexeme(), t.type(), t.span(), List.of(), List.of());
        }

        if (p.match(TokenType.STRING)) {
            Token t = p.previous();
            String lexeme = t.lexeme().substring(1, t.lexeme().length() - 1); // remove quotes
            return new LiteralExpression(lexeme, t.type(), t.span(), List.of(), List.of());
        }

        if (p.match(TokenType.NIL)) {
            Token t = p.previous();
            return new LiteralExpression("nil", t.type(), t.span(), List.of(), List.of());
        }

        if (p.match(TokenType.TRUE)) {
            Token t = p.previous();
            return new LiteralExpression("true", t.type(), t.span(), List.of(), List.of());
        }

        if (p.match(TokenType.FALSE)) {
            Token t = p.previous();
            return new LiteralExpression("false", t.type(), t.span(), List.of(), List.of());
        }

        if (p.match(TokenType.IDENTIFIER)) {
            Token id = p.previous();
            return new IdentifierExpression(id.lexeme(), id.span(), List.of(), List.of());
        }

        if (p.match(TokenType.VARARG)) {
            Token t = p.previous();
            return new VarArgExpression(t.span(), List.of(), List.of());
        }

        // handle '{'
        if (p.match(TokenType.LBRACE)) {
            return parseTableConstructor();
        }

        if (p.match(TokenType.LPAREN)) {
            Token t = p.previous();
            Expression e = parseExpression();
            p.consume(TokenType.RPAREN, "expected ')'");
            return new ParanthesisExpression(e, t.span(), List.of(), List.of());
        }


        throw p.error(p.peek(), "unexpected token in expression");
    }

    public Expression parsePrefixExpression() {
        Expression expr = parsePrimary();

        while (true) {

            if (p.isLookaheadOnNewLine()) {
                if (p.check(TokenType.LPAREN) || p.check(TokenType.LBRACKET)) {
                    break;
                }
            }

            // call suffixes: ( args )
            if (p.match(TokenType.LPAREN)) {
                List<Expression> args = new ArrayList<>();

                if (!p.check(TokenType.RPAREN)) {
                    args = parseExpressionList();
                }

                Token rp = p.consume(TokenType.RPAREN, "expected ')' after argument list");

                Span span = new Span(
                        expr.getSpan().getStartOffset(),
                        rp.span().getEndOffset(),
                        expr.getSpan().getStart(),
                        rp.span().getEnd()
                );

                expr = new FunctionCallExpression(expr, args, span, List.of(), List.of());
            }

            // table index or field access
            else if (p.match(TokenType.LBRACKET)) {
                Expression key = parseExpression();
                p.consume(TokenType.RBRACKET, "expected ']' after table key");

                Span span = new Span(
                        expr.getSpan().getStartOffset(),
                        p.previous().span().getEndOffset(),
                        expr.getSpan().getStart(),
                        p.previous().span().getEnd()
                );

                expr = new TableAccessExpression(expr, key, false, true, span, List.of(), List.of());
            } else if (p.match(TokenType.DOT)) {
                Token fieldTok = p.consume(TokenType.IDENTIFIER, "expected identifier after '.'");

                // We wrap the identifiers lexeme in a string literal
                // because table.name is semantically table["name"]
                Expression key = new IdentifierExpression(fieldTok.lexeme(), fieldTok.span(), List.of(), List.of());

                Span span = new Span(
                        expr.getSpan().getStartOffset(),
                        fieldTok.span().getEndOffset(),
                        expr.getSpan().getStart(),
                        fieldTok.span().getEnd()
                );

                expr = new TableAccessExpression(expr, key, true, false, span, List.of(), List.of());
            } else if (p.match(TokenType.COLON)) {
                Token methodTok = p.consume(TokenType.IDENTIFIER, "expected method name after ':'");
                p.consume(TokenType.LPAREN, "expected '(' after method name");
                List<Expression> args = new ArrayList<>();

                if (!p.check(TokenType.RPAREN)) {
                    args = parseExpressionList();
                }

                Token rp = p.consume(TokenType.RPAREN, "expected ')' after method arguments");

                Span span = new Span(
                        expr.getSpan().getStartOffset(),
                        rp.span().getEndOffset(),
                        expr.getSpan().getStart(),
                        rp.span().getEnd()
                );

                Expression methodName = new IdentifierExpression(methodTok.lexeme(), methodTok.span(), List.of(), List.of());
                expr = new MethodCallExpression(expr, methodName, args, span, List.of(), List.of());
            } else {
                break;
            }
        }

        return expr;
    }

    private Expression parseTableConstructor() {
        Token openBrace = p.previous(); // The '{'
        List<TableFieldExpression> fields = new ArrayList<>();

        // table ::= '{' [fieldlist] '}'
        // fieldlist ::= field {fieldsep field} [fieldsep]
        // fieldsep ::= ',' | ';'

        while (!p.check(TokenType.RBRACE) && !p.check(TokenType.EOF)) {
            fields.add(parseTableField());

            // Fields can be separated by commas or semicolons
            if (!p.match(TokenType.COMMA, TokenType.SEMICOLON)) {
                break;
            }
        }

        Token closeBrace = p.consume(TokenType.RBRACE, "expected '}' after table constructor");
        Span span = p.combine(openBrace, closeBrace);

        return new TableConstructorExpression(fields, span, List.of(), List.of());
    }

    private TableFieldExpression parseTableField() {
        Expression key = null;
        Expression value;
        Token startTok = p.peek();

        // [expr] = expr (Explicit Key)
        if (p.match(TokenType.LBRACKET)) {
            key = parseExpression();
            p.consume(TokenType.RBRACKET, "expected ']' after table key");
            p.consume(TokenType.ASSIGN, "expected '=' after table key");
            value = parseExpression();
        }
        // name = expr (Sugar for string key)
        else if (p.check(TokenType.IDENTIFIER) && p.peekNext().type() == TokenType.ASSIGN) {
            Token id = p.consume(TokenType.IDENTIFIER, "expected identifier");
            // We wrap the identifier's lexeme in a string literal
            // because table.name is semantically table["name"]
            key = new IdentifierExpression(id.lexeme(), id.span(), List.of(), List.of());
            p.consume(TokenType.ASSIGN, "expected '=' after field name");
            value = parseExpression();
        }
        // expr (List-style, implicit numeric key)
        else {
            value = parseExpression();
        }

        Span span = p.combine(startTok, p.previous());
        return new TableFieldExpression(key, value, span, List.of(), List.of());
    }

    private Expression parseAnonymousFunction() {
        Token funcToken = p.previous();

        p.consume(TokenType.LPAREN, "expected '(' after 'function'");

        List<Expression> parameters = new ArrayList<>();
        boolean isVarArg = false;

        if (!p.check(TokenType.RPAREN)) {
            do {
                if (p.match(TokenType.VARARG)) {
                    isVarArg = true;
                    break;
                }
                var paramExpr = p.expr.parseExpressionList();
                parameters.addAll(paramExpr);
            } while (p.match(TokenType.COMMA));
        }

        p.consume(TokenType.RPAREN, "expected ')' after parameters");

        List<Statement> body = p.parseChunk().getBlock().getStatements();

        Token endToken = p.consume(TokenType.END, "expected 'end' to close function");

        Span span = new Span(
                funcToken.span().getStartOffset(),
                endToken.span().getEndOffset(),
                funcToken.span().getStart(),
                endToken.span().getEnd()
        );

        var newBlock = new Block(body, span, List.of(), List.of());


        var anonFunc = new AnonymousFunctionExpression(parameters, new Chunk(newBlock, span, List.of(), List.of()), span, List.of(), List.of());

        for (var expr : parameters) expr.setParent(anonFunc);

        return anonFunc;
    }

    // =======================================================
    // Helpers
    // =======================================================

    private Expression makeBinary(Expression left, Token op, Expression right) {
        Span span = new Span(
                left.getSpan().getStartOffset(),
                right.getSpan().getEndOffset(),
                left.getSpan().getStart(),
                right.getSpan().getEnd()
        );
        return new BinaryExpression(left, op.type(), right, span, List.of(), List.of());
    }

    private boolean isNumericLiteral(String text) {
        // Very simple check: digits with optional single dot
        // (will later extend this for hex, exponent, etc.)
        if (text == null || text.isEmpty()) return false;

        int i = 0;
        int len = text.length();
        boolean hasDigit = false;
        boolean hasDot = false;

        while (i < len) {
            char c = text.charAt(i++);
            if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (c == '.') {
                if (hasDot) return false; // second dot
                hasDot = true;
            } else {
                return false;
            }
        }
        return hasDigit;
    }
}
