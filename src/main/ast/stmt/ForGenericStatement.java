package main.ast.stmt;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.ast.exp.Expression;
import main.ast.exp.IdentifierExpression;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>{@code
 * for List<IdentifierExpression> in ipairs(a) do
 *     <Block>
 * end
 * }</pre>
 */
public class ForGenericStatement extends Statement {


    private final List<IdentifierExpression> names = new ArrayList<>();
    private final Expression expression;
    private final Block body;

    public ForGenericStatement(List<IdentifierExpression> names,
                       Expression expression,
                               Block body,
                       Span span,
                       List<Comment> lead,
                       List<Comment> trail) {
        super(NodeKind.FOR_GENERIC_STATEMENT, span, lead, trail);
        this.getNames().addAll(adoptAll(names));
        this.expression = adoptChild(expression);
        this.body = adoptChild(body);
    }

    public ForGenericStatement(List<IdentifierExpression> names,
                               Expression expression,
                               Block body) {
        super(NodeKind.FOR_GENERIC_STATEMENT);
        this.getNames().addAll(adoptAll(names));
        this.expression = adoptChild(expression);
        this.body = adoptChild(body);
    }

    public List<IdentifierExpression> getNames() {
        return names;
    }
    public Expression getExpression() {
        return expression;
    }
    public Block getBody() {
        return body;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.addAll(body.getStatements());
        out.add(expression);
        out.addAll(names);
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitForGeneric(this);
    }


}
