package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class TableFieldExpression extends Expression {

    private final Expression key;
    private final Expression value;

    public TableFieldExpression(Expression key,
                                Expression value,
                                Span span,
                                List<Comment> lead,
                                List<Comment> trail) {
        super(NodeKind.TABLE_FIELD, span, lead, trail);
        this.key = adoptChild(key);
        this.value = adoptChild(value);
    }

    public TableFieldExpression(Expression key,
                                Expression value) {
        super(NodeKind.TABLE_FIELD);
        this.key = adoptChild(key);
        this.value = adoptChild(value);
    }

    public Expression getKey() {
        return key;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> children = new ArrayList<>();
        if (key != null) {
            children.add(key);
        }
        children.add(value);
        return children;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitTableField(this);
    }

    @Override
    public String toString() {
        if (key != null) {
            return "[" + key + "] = " + value;
        } else {
            return value.toString();
        }
    }

}