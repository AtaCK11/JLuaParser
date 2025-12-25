package main.ast.stmt;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.List;

/**
 * <pre>{@code
 * break
 * }</pre>
 */
public class BreakStatement extends Statement {


    public BreakStatement(Span span,
                          List<Comment> lead,
                          List<Comment> trail) {
        super(NodeKind.BREAK_STATEMENT, span, lead, trail);
    }

    public BreakStatement() {
        super(NodeKind.BREAK_STATEMENT);
    }


    @Override
    public List<Node> getChildren() {
        return List.of();
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitBreak(this);
    }

}