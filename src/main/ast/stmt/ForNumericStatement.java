package main.ast.stmt;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.ast.exp.Expression;
import main.ast.exp.IdentifierExpression;
import main.util.Comment;
import main.util.Span;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>{@code
 * for <IdentifierExpression> = <Expression>, <Expression>, Optional(<Expression>) do
 *     <Block>
 * end
 * }</pre>
 */
public class ForNumericStatement extends Statement {

    private final IdentifierExpression variable;
    private final Expression start;
    private final Expression end;
    private final Block body;

    @Nullable
    private final Expression step;

    public ForNumericStatement(IdentifierExpression variable, Expression start, Expression end, Expression step,
                               Block body,
                               Span span,
                               List<Comment> lead,
                               List<Comment> trail) {
        super(NodeKind.FOR_NUMERIC_STATEMENT, span, lead, trail);
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = adoptChild(body);
    }

    public ForNumericStatement(IdentifierExpression variable, Expression start, Expression end, Expression step,
                               Block body) {
        super(NodeKind.FOR_NUMERIC_STATEMENT);
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = adoptChild(body);
    }

    public ForNumericStatement(IdentifierExpression variable, Expression start, Expression end,
                               Block body) {
        super(NodeKind.FOR_NUMERIC_STATEMENT);
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.step = null;
        this.body = adoptChild(body);
    }

    public IdentifierExpression getVariable() {
        return variable;
    }

    public Expression getStart() {
        return start;
    }

    public Expression getEnd() {
        return end;
    }

    public Expression getStep() {
        return step;
    }

    public Block getBody() {
        return body;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.add(body);
        out.add(start);
        out.add(end);
        if (step != null) {
            out.add(step);
        }
        out.add(variable);
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitForNumeric(this);
    }

}
