package main.ast.stmt;

import main.ast.*;
import main.ast.exp.Expression;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.List;

public final class ExpressionStatement extends Statement {

    private final Expression expr;

    public ExpressionStatement(Expression expr,
                               Span span,
                               List<Comment> lead,
                               List<Comment> trail) {
        super(NodeKind.EXPRESSION_STATEMENT, span, lead, trail);
        this.expr = adoptChild(expr);
    }

    public ExpressionStatement(Expression expr) {
        super(NodeKind.EXPRESSION_STATEMENT);
        this.expr = adoptChild(expr);
    }

    public Expression getExpression() {
        return expr;
    }

    @Override
    public List<Node> getChildren() {
        return List.of(expr);
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitExpressionStatement(this);
    }
}
