package main.parser;

import main.ast.*;
import main.ast.exp.*;
import main.ast.stmt.*;
import main.lexer.Token;
import main.lexer.TokenType;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class StatementParser {

    private final Parser p;

    public StatementParser(Parser parser) {
        this.p = parser;
    }

    // =======================================================
    // Main dispatcher
    // =======================================================

    public Statement parseStatement() {
        if (p.match(TokenType.LOCAL)) {
            return parseLocalStatement();
        }

        if (p.match(TokenType.FUNCTION)) {
            return parseFunctionStatement();
        }

        if (p.match(TokenType.RETURN)) {
            return parseReturnStatement();
        }

        if (p.match(TokenType.BREAK)) {
            return parseBreakStatement();
        }

        if (p.match(TokenType.CONTINUE)) {
            return parseContinueStatement();
        }

        if (p.match(TokenType.DO)) {
            return parseDoStatement();
        }

        if (p.match(TokenType.IF)) {
            return parseIfStatement();
        }

        if (p.match(TokenType.WHILE)) {
            return parseWhileStatement();
        }

        if (p.match(TokenType.REPEAT)) {
            return parseRepeatStatement();
        }

        if (p.match(TokenType.FOR)) {
            return parseForStatement();
        }

        // for now: everything else starts with an expression
        // (assignment or function call)
        return parsePrefixStatement();
    }

    // =======================================================
    // local x, y = ...
    // =======================================================

    private Statement parseLocalStatement() {
        Token localTok = p.previous();

        if (p.match(TokenType.FUNCTION)) {
            return parseLocalFunctionStatement();
        }

        List<String> names = new ArrayList<>();
        names.add(p.consume(TokenType.IDENTIFIER, "expected identifier after 'local'").lexeme());

        while (p.match(TokenType.COMMA)) {
            names.add(p.consume(TokenType.IDENTIFIER, "expected identifier").lexeme());
        }

        List<Expression> values = List.of();
        if (p.match(TokenType.ASSIGN)) {
            values = p.expr.parseExpressionList();
        }

        Token last = p.previous();
        Span span = p.combine(localTok, last);

        var identifiers = new ArrayList<IdentifierExpression>();
        for (String name : names) {
            var idExpr = new IdentifierExpression(name, span, List.of(), List.of());
            identifiers.add(idExpr);
        }

        var localStatement = new LocalStatement(identifiers, values, span, List.of(), List.of());
        for (var idExpr : identifiers) {
            idExpr.setParent(localStatement);
        }

        return localStatement;
    }

    // =======================================================
    // local function name(...) ... end
    // =======================================================

    private Statement parseLocalFunctionStatement() {
        Token localTok = p.previous();
        Token nameTok = p.consume(TokenType.IDENTIFIER, "expected function name after 'local function'");
        String name = nameTok.lexeme();

        p.consume(TokenType.LPAREN, "expected '(' after function name");

        List<Expression> parameters = new ArrayList<>();
        if (!p.check(TokenType.RPAREN)) {
            do {
                var paramExpr = p.expr.parseExpressionList();
                parameters.addAll(paramExpr);
            } while (p.match(TokenType.COMMA));
        }

        p.consume(TokenType.RPAREN, "expected ')' after parameter list");

        Block body = p.parseChunk().getBlock();

        Token endTok = p.consume(TokenType.END, "expected 'end' after function body");
        Span span = p.combine(localTok, endTok);

        var nameIdentifier = new IdentifierExpression(name, nameTok.span(), List.of(), List.of());
        var chunk = new Chunk(body, span, List.of(), List.of());
        var localFuncStmt = new LocalFunctionStatement(nameIdentifier, parameters, chunk, span, List.of(), List.of());

        nameIdentifier.setParent(localFuncStmt);
        for (var paramExpr : parameters) {
            paramExpr.setParent(localFuncStmt);
        }

        return localFuncStmt;
    }

    // =======================================================
    // function name(...) ... end
    // =======================================================

    private Statement parseFunctionStatement() {
        Token funcTok = p.previous();

        Expression nameExpr = new IdentifierExpression(
                p.consume(TokenType.IDENTIFIER, "expected function name").lexeme(),
                p.previous().span(), List.of(), List.of()
        );

        while (p.match(TokenType.DOT)) {
            Token dot = p.previous();
            Token field = p.consume(TokenType.IDENTIFIER, "expected field name after '.'");
            IdentifierExpression key = new IdentifierExpression(field.lexeme(), field.span(), List.of(), List.of());

            nameExpr = new TableAccessExpression(nameExpr, key, true, false, p.combine(nameExpr, field), List.of(), List.of());
        }

        boolean isMethod = false;
        if (p.match(TokenType.COLON)) {
            Token colon = p.previous();
            Token field = p.consume(TokenType.IDENTIFIER, "expected method name after ':'");
            IdentifierExpression key = new IdentifierExpression(field.lexeme(), field.span(), List.of(), List.of());

            nameExpr = new MethodDefinitionExpression(nameExpr, key, p.combine(nameExpr, field), List.of(), List.of());
            isMethod = true;
        }

        p.consume(TokenType.LPAREN, "expected '(' after function name");

        List<Expression> parameters = new ArrayList<>();
        if (!p.check(TokenType.RPAREN)) {
            do {
                var paramExpr = p.expr.parseExpressionList();
                parameters.addAll(paramExpr);
            } while (p.match(TokenType.COMMA));
        }

        p.consume(TokenType.RPAREN, "expected ')' after parameter list");

        Block body = p.parseChunk().getBlock();

        Token endTok = p.consume(TokenType.END, "expected 'end' after function body");
        Span span = p.combine(funcTok, endTok);

        var chunk = new Chunk(body, span, List.of(), List.of());
        var funcStmt = new FunctionStatement(nameExpr, parameters, chunk, span, List.of(), List.of());

        nameExpr.setParent(funcStmt);
        for (var paramExpr : parameters) {
            paramExpr.setParent(funcStmt);
        }

        return funcStmt;
    }

    // =======================================================
    // return ...
    // =======================================================

    private Statement parseReturnStatement() {
        Token retTok = p.previous();

        List<Expression> values = List.of();
        var a = p.peek();
        var b = p.isReturnTerminator();
        if (!p.isReturnTerminator()) {
            values = p.expr.parseExpressionList();
        }

        Token last = p.previous();
        Span span = p.combine(retTok, last);

        return new ReturnStatement(values, span, List.of(), List.of());
    }

    // =======================================================
    // break
    // =======================================================

    private Statement parseBreakStatement() {
        Token breakTok = p.previous();
        Span span = breakTok.span();

        return new BreakStatement(span, List.of(), List.of());
    }

    // =======================================================
    // continue
    // =======================================================

    private Statement parseContinueStatement() {
        Token continueTok = p.previous();
        Span span = continueTok.span();

        return new ContinueStatement(span, List.of(), List.of());
    }

    // =======================================================
    // do ... end
    // =======================================================

    private Statement parseDoStatement() {
        Token doTok = p.previous();
        Block body = p.parseChunk().getBlock();
        Token endTok = p.consume(TokenType.END, "expected 'end' after do body");
        Span span = p.combine(doTok, endTok);

        return new DoStatement(body, span, List.of(), List.of());
    }

    // =======================================================
    // if ... then ... [elseif ... then ...] [else ...] end
    // =======================================================

    private Statement parseIfStatement() {
        Token ifTok = p.previous();
        Expression condition = p.expr.parseExpression();
        p.consume(TokenType.THEN, "expected 'then' after if condition");
        Block body = p.parseChunk().getBlock();

        List<ElseIfStatement> elseIfs = new ArrayList<>();
        while (p.match(TokenType.ELSEIF)) {
            Token elseifTok = p.previous();
            Expression elseifCondition = p.expr.parseExpression();
            p.consume(TokenType.THEN, "expected 'then' after elseif condition");
            Block elseifBody = p.parseChunk().getBlock();
            Span elseifSpan = p.combine(elseifTok, p.previous());
            ElseIfStatement elseIfStmt = new ElseIfStatement(elseifCondition, elseifBody, elseifSpan, List.of(), List.of());
            elseIfs.add(elseIfStmt);
        }

        ElseStatement elseStmt = null;
        if (p.match(TokenType.ELSE)) {
            Token elseTok = p.previous();
            Block elseBody = p.parseChunk().getBlock();
            Span elseSpan = p.combine(elseTok, p.previous());
            elseStmt = new ElseStatement(elseBody, elseSpan, List.of(), List.of());
        }

        Token endTok = p.consume(TokenType.END, "expected 'end' after if statement");
        Span span = p.combine(ifTok, endTok);

        return new IfStatement(condition, body, elseIfs, elseStmt, span, List.of(), List.of());
    }

    // =======================================================
    // while expr do ... end
    // =======================================================

    private Statement parseWhileStatement() {
        Token whileTok = p.previous();
        Expression condition = p.expr.parseExpression();
        p.consume(TokenType.DO, "expected 'do' after while condition");
        Block body = p.parseChunk().getBlock();
        Token endTok = p.consume(TokenType.END, "expected 'end' after while body");
        Span span = p.combine(whileTok, endTok);

        return new WhileStatement(condition, body, span, List.of(), List.of());
    }

    // =======================================================
    //     repeat ... until expr
    // =======================================================

    private Statement parseRepeatStatement() {
        Token repeatTok = p.previous();
        Block body = p.parseChunk().getBlock();
        p.consume(TokenType.UNTIL, "expected 'until' after repeat body");
        Expression condition = p.expr.parseExpression();
        Token lastTok = p.previous();
        Span span = p.combine(repeatTok, lastTok);

        return new RepeatStatement(condition, body, span, List.of(), List.of());
    }

    // =======================================================
    // for ... do ... end
    // =======================================================

    private Statement parseForStatement() {
        // Determine if it's a numeric for or generic for
        var currentToken = p.peek();
        var nextToken = p.tokens.get(p.current+1);

        if (nextToken != null && nextToken.type() == TokenType.ASSIGN) {
            // Numeric for
            return parseNumericForStatement();
        } else {
            return parseGenericForStatement();
        }
    }

    // =======================================================
    // for i = start, end, step do statement end
    // =======================================================

    private Statement parseNumericForStatement() {
        Token forTok = p.previous();
        var varName = p.consume(TokenType.IDENTIFIER, "expected identifier after 'for'").lexeme();
        p.consume(TokenType.ASSIGN, "expected '=' after for variable");
        var start = p.expr.parseExpression();
        p.consume(TokenType.COMMA, "expected ',' after for start expression");
        var end = p.expr.parseExpression();
        Expression step = null;
        if (p.match(TokenType.COMMA)) {
            step = p.expr.parseExpression();
        }
        p.consume(TokenType.DO, "expected 'do' after for clauses");
        Block body = p.parseChunk().getBlock();

        Token endTok = p.consume(TokenType.END, "expected 'end' after for body");
        Span span = p.combine(forTok, endTok);
        var varNameIdentifier = new IdentifierExpression(varName, p.combine(forTok, forTok), List.of(), List.of());
        var forNumericStat = new ForNumericStatement(varNameIdentifier, start, end, step, body,  span, List.of(), List.of());
        varNameIdentifier.setParent(forNumericStat);
        return forNumericStat;
    }

    // =======================================================
    // for vars in function do ... end
    // =======================================================

    private Statement parseGenericForStatement() {
        var forTok = p.previous();
        List<IdentifierExpression> varNames = new ArrayList<>();
        var firstVarName = p.consume(TokenType.IDENTIFIER, "expected identifier after 'for'");
        var var1NameExpr = new IdentifierExpression(firstVarName.lexeme(), firstVarName.span(), List.of(), List.of());
        varNames.add(var1NameExpr);
        while (p.match(TokenType.COMMA)) {
            var varNameToken = p.consume(TokenType.IDENTIFIER, "expected identifier");
            varNames.add(new IdentifierExpression(varNameToken.lexeme(), varNameToken.span(), List.of(), List.of()));
        }
        p.consume(TokenType.IN, "expected 'in' after for variable list");
        Expression iterator = p.expr.parseExpression();
        p.consume(TokenType.DO, "expected 'do' after for clauses");
        Block body = p.parseChunk().getBlock();
        Token endTok = p.consume(TokenType.END, "expected 'end' after for body");
        Span span = p.combine(forTok, endTok);

        var forGenericStat = new ForGenericStatement(varNames, iterator, body, span, List.of(), List.of());

        for (var nameExpr : varNames) {
            nameExpr.setParent(forGenericStat);
        }

        return forGenericStat;

    }

    // =======================================================
    // Prefix-based statements:
    //   - assignment:  a, b = ...
    //   - call:        foo(x)
    // =======================================================

    private Statement parsePrefixStatement() {
        List<Expression> vars = new ArrayList<>();
        vars.add(p.expr.parsePrefixExpression());

        while (p.match(TokenType.COMMA)) {
            vars.add(p.expr.parsePrefixExpression());
        }

        if (p.match(TokenType.ASSIGN)) {
            List<Expression> values = p.expr.parseExpressionList();

            Span span = p.combine(vars.get(0), values.get(values.size() - 1));
            return new AssignmentStatement(vars, values, span, List.of(), List.of());
        }

        if (vars.size() == 1) {
            Expression first = vars.get(0);
            if (first instanceof FunctionCallExpression || first instanceof MethodCallExpression) {
                return new ExpressionStatement(first, first.getSpan(), List.of(), List.of());
            }
        }

        throw p.error(p.peek(), "unexpected expression in statement");
    }
}
