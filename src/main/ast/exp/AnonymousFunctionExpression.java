package main.ast.exp;

import main.ast.Chunk;
import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class AnonymousFunctionExpression extends Expression {

    private final Chunk chunk;
    private final List<Expression> parameters = new ArrayList<>();

    public AnonymousFunctionExpression(List<Expression> parameters,
                                       Chunk chunk,
                                       Span span,
                                       List<Comment> lead,
                                       List<Comment> trail) {
        super(NodeKind.ANONYMOUS_FUNCTION, span, lead, trail);
        this.parameters.addAll(parameters);
        this.chunk = chunk;
    }

    public AnonymousFunctionExpression(List<Expression> parameters,
                                       Chunk chunk) {
        super(NodeKind.ANONYMOUS_FUNCTION);
        this.parameters.addAll(parameters);
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> children = new ArrayList<>();
        children.addAll(parameters);
        children.add(chunk);
        return children;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitAnonymousFunction(this);
    }

    @Override
    public String toString() {
        return "function(...) ... end";
    }
}
