package main.ast.stmt;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.ast.exp.Expression;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;
/**
 * <pre>{@code
 * a = 1
 * a,b = 2,1
 * }</pre>
 */
public final class AssignmentStatement extends Statement {

    private final List<Expression> variables;
    private final List<Expression> values;

    public AssignmentStatement(List<Expression> vars,
                               List<Expression> values,
                               Span span,
                               List<Comment> lead,
                               List<Comment> trail) {
        super(NodeKind.ASSIGNMENT_STATEMENT, span, lead, trail);
        this.variables   = adoptAll(vars);
        this.values = adoptAll(values);
    }

    public AssignmentStatement(List<Expression> vars,
                               List<Expression> values) {
        super(NodeKind.ASSIGNMENT_STATEMENT);
        this.variables   = adoptAll(vars);
        this.values = adoptAll(values);
    }

    public List<Expression> getVariables() {
        return variables;
    }

    public List<Expression> getValues() {
        return values;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.addAll(variables);
        out.addAll(values);
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitAssignment(this);
    }
}
