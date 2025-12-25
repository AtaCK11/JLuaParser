package main.ast.stmt;

import main.ast.*;
import main.ast.exp.Expression;

import main.ast.exp.IdentifierExpression;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>{@code
 * local List<IdentifierExpression> = List<Expression>
 * }</pre>
 */
public final class LocalStatement extends Statement {

    private final List<IdentifierExpression> variables;
    private final List<Expression> values;

    public LocalStatement(List<IdentifierExpression> names,
                          List<Expression> values,
                          Span span,
                          List<Comment> lead,
                          List<Comment> trail) {
        super(NodeKind.LOCAL_STATEMENT, span, lead, trail);
        this.variables  = List.copyOf(names);
        this.values = adoptAll(values);
    }

    public LocalStatement(List<IdentifierExpression> names,
                          List<Expression> values) {
        super(NodeKind.LOCAL_STATEMENT);
        this.variables  = List.copyOf(names);
        this.values = adoptAll(values);
    }

    public List<IdentifierExpression> getVariables() {
        return variables;
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
        return nodeVisitor.visitLocal(this);
    }
}
