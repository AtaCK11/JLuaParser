package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.List;

public final class ParanthesisExpression extends Expression {

    private final Expression innerExpr;

    public ParanthesisExpression(Expression expr,
                                 Span span,
                                 List<Comment> lead,
                                 List<Comment> trail) {
        super(NodeKind.PARENTHESIS_EXPRESSION, span, lead, trail);
        this.innerExpr = adoptChild(expr);
    }

    public ParanthesisExpression(Expression expr) {
        super(NodeKind.PARENTHESIS_EXPRESSION);
        this.innerExpr = adoptChild(expr);
    }

    public Expression getInnerExpr() {
        return innerExpr;
    }

    @Override
    public List<Node> getChildren() {
        return List.of(innerExpr);
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitParanthesis(this);
    }

    @Override
    public String toString() {
        return "(" + innerExpr + ")";
    }
}