package main.ast.stmt;

import main.ast.Chunk;
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
 * function <Expression>(List<Expression>)
 *     <Chunk>
 * end
 * }</pre>
 */
public final class FunctionStatement extends Statement {

    private final Expression name;
    private final Chunk chunk;
    private final List<Expression> parameters = new ArrayList<>();

    public FunctionStatement(Expression name,
                             List<Expression> parameters,
                             Chunk chunk,
                             Span span,
                             List<Comment> lead,
                             List<Comment> trail) {
        super(NodeKind.FUNCTION_STATEMENT, span, lead, trail);
        this.name  = name;
        this.parameters.addAll(parameters);
        this.chunk = chunk;
    }

    public FunctionStatement(Expression name,
                             List<Expression> parameters,
                             Chunk chunk) {
        super(NodeKind.FUNCTION_STATEMENT);
        this.name  = name;
        this.parameters.addAll(parameters);
        this.chunk = chunk;
    }

    public Expression getName() {
        return name;
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
        children.add(name);
        children.addAll(parameters);
        children.add(chunk);
        return children;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitFunction(this);
    }
}
