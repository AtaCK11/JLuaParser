package main.ast.stmt;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

/**
 * Statement List
 */
public final class Block extends Statement {

    private final List<Statement> statements;

    public Block(List<Statement> statements,
                 Span span,
                 List<Comment> lead,
                 List<Comment> trail) {
        super(NodeKind.BLOCK, span, lead, trail);
        this.statements = adoptAll(statements);
    }

    public Block(List<Statement> statements) {
        super(NodeKind.BLOCK);
        this.statements = adoptAll(statements);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>(statements);
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitBlock(this);
    }
}
