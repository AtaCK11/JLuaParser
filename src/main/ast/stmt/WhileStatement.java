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
 * while <Expression> do
 *     <Block>
 * end
 * }</pre>
 */
public class WhileStatement extends Statement {

    private final Block body;
    private final Expression expression;

    public WhileStatement(Expression expressions,
                          Block body,
                          Span span,
                          List<Comment> lead,
                          List<Comment> trail) {
        super(NodeKind.WHILE_STATEMENT, span, lead, trail);
        this.body = adoptChild(body);
        this.expression = adoptChild(expressions);
    }

    public WhileStatement(Expression expression,
                          Block body) {
        super(NodeKind.WHILE_STATEMENT);
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
        return nodeVisitor.visitWhile(this);
    }

}
