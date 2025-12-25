package main.ast.stmt;

import main.ast.*;
import main.ast.exp.Expression;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>{@code
 * return List<Expression>
 * }</pre>
 */
public final class ReturnStatement extends Statement {

    private final List<Expression> values;

    public ReturnStatement(List<Expression> values,
                           Span span,
                           List<Comment> lead,
                           List<Comment> trail) {
        super(NodeKind.RETURN_STATEMENT, span, lead, trail);
        this.values = adoptAll(values);
    }

    public ReturnStatement(List<Expression> values) {
        super(NodeKind.RETURN_STATEMENT);
        this.values = adoptAll(values);
    }

    public List<Expression> getValues() {
        return values;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>(values);
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitReturn(this);
    }
}
