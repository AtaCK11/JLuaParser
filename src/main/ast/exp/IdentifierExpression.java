package main.ast.exp;

import main.ast.*;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.List;

public final class IdentifierExpression extends Expression {

    private final String name;

    public IdentifierExpression(String name,
                                Span span,
                                List<Comment> lead,
                                List<Comment> trail) {
        super(NodeKind.IDENTIFIER, span, lead, trail);
        this.name = name;
    }

    public IdentifierExpression(String name) {
        super(NodeKind.IDENTIFIER);
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public List<Node> getChildren() {
        return List.of();
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitIdentifier(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
