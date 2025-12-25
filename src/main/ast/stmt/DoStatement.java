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
 * do
 *     <Block>
 * end
 * }</pre>
 */
public class DoStatement extends Statement {

    private final Block body;

    public DoStatement(Block body,
                       Span span,
                       List<Comment> lead,
                       List<Comment> trail) {
        super(NodeKind.DO_STATEMENT, span, lead, trail);
        this.body = adoptChild(body);
    }

    public DoStatement(Block body) {
        super(NodeKind.DO_STATEMENT);
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
        return nodeVisitor.visitDo(this);
    }

}