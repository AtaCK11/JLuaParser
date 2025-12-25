package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.List;

public final class VarArgExpression extends Expression {


    public VarArgExpression(Span span,
                           List<Comment> lead,
                           List<Comment> trail) {
        super(NodeKind.VARARG, span, lead, trail);
    }

    public VarArgExpression() {
        super(NodeKind.VARARG);
    }

    @Override
    public List<Node> getChildren() {
        return List.of();
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitVarArg(this);
    }

    @Override
    public String toString() {
        return "...";
    }
}
