package main.ast.stmt;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>{@code
 * else
 *     <Block>
 * end
 * }</pre>
 */
public class ElseStatement extends Statement {

    private final Block body;

    public ElseStatement(Block body,
                       Span span,
                       List<Comment> lead,
                       List<Comment> trail) {
        super(NodeKind.ELSE_STATEMENT, span, lead, trail);
        this.body = adoptChild(body);
    }

    public ElseStatement(Block body) {
        super(NodeKind.ELSE_STATEMENT);
        this.body = adoptChild(body);
    }

    public Block getBody() {
        return body;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.addAll(body.getStatements());
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitElse(this);
    }

}
