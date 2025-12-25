package main.ast.exp;

import main.ast.*;
import main.lexer.TokenType;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.List;

public final class UnaryExpression extends Expression {

    private final TokenType op;
    private final Expression expr;

    public UnaryExpression(TokenType op,
                           Expression expr,
                           Span span,
                           List<Comment> lead,
                           List<Comment> trail) {
        super(NodeKind.UNARY, span, lead, trail);
        this.op = op;
        this.expr = adoptChild(expr);
    }

    public UnaryExpression(TokenType op,
                           Expression expr) {
        super(NodeKind.UNARY);
        this.op = op;
        this.expr = adoptChild(expr);
    }

    public TokenType getOp() {
        return op;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public List<Node> getChildren() {
        return List.of(expr);
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitUnary(this);
    }

    @Override
    public String toString() {
        return "(" + op + " " + expr + ")";
    }
}
