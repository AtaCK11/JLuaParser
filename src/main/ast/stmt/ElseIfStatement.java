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
 * elseif <Expression>
 *     <Block>
 * end
 * }</pre>
 */
public class ElseIfStatement extends Statement {

    private final Block body;
    private final Expression expression;

    public ElseIfStatement(Expression expressions,
                           Block body,
                           Span span,
                           List<Comment> lead,
                           List<Comment> trail) {
        super(NodeKind.ELSEIF_STATEMENT, span, lead, trail);
        this.body = adoptChild(body);
        this.expression = adoptChild(expressions);
    }

    public ElseIfStatement(Expression expression,
                           Block body) {
        super(NodeKind.ELSEIF_STATEMENT);
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
        out.addAll(body.getStatements());
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitElseIf(this);
    }

}