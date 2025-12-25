package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class TableAccessExpression extends Expression {

    private final Expression name;
    private final Expression index;
    private final boolean isDotAccess;
    private final boolean isBracketAccess;

    public TableAccessExpression(Expression name,
                                 Expression index,
                                 boolean isDotAccess,
                                 boolean isBracketAccess,
                                 Span span,
                                 List<Comment> lead,
                                 List<Comment> trail) {
        super(NodeKind.TABLE_FIELD, span, lead, trail);
        this.name = adoptChild(name);
        this.index = adoptChild(index);
        this.isDotAccess = isDotAccess;
        this.isBracketAccess = isBracketAccess;
    }

    public TableAccessExpression(Expression name,
                                 Expression index,
                                 boolean isDotAccess,
                                 boolean isBracketAccess) {
        super(NodeKind.TABLE_FIELD);
        this.name = adoptChild(name);
        this.index = adoptChild(index);
        this.isDotAccess = isDotAccess;
        this.isBracketAccess = isBracketAccess;
    }

    public Expression getName() {
        return name;
    }

    public Expression getIndex() {
        return index;
    }

    public boolean isDotAccess() {
        return isDotAccess;
    }

    public boolean isBracketAccess() {
        return isBracketAccess;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> children = new ArrayList<>();
        if (name != null) {
            children.add(name);
        }
        children.add(index);
        return children;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitTableAccess(this);
    }

    @Override
    public String toString() {
        if (isDotAccess) {
            return name.toString() + "." + index.toString();
        } else if (isBracketAccess) {
            return name.toString() + "[" + index.toString() + "]";
        } else {
            return name.toString() + index.toString();
        }
    }

}
