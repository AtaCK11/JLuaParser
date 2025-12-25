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
 * repeat
 *     <Block>
 * until <Expression>
 * }</pre>
 */
public class RepeatStatement extends Statement {

    private final Block body;
    private final Expression expression;

    public RepeatStatement(Expression expression,
                           Block body,
                           Span span,
                           List<Comment> lead,
                           List<Comment> trail) {
        super(NodeKind.REPEAT_STATEMENT, span, lead, trail);
        this.body = adoptChild(body);
        this.expression = adoptChild(expression);
    }

    public RepeatStatement(Expression expression,
                           Block body) {
        super(NodeKind.REPEAT_STATEMENT);
        this.body = adoptChild(body);
        this.expression = adoptChild(expression);
    }

    public Block getBody() {
        return body;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.add(expression);
        out.add(body);
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitRepeat(this);
    }

}