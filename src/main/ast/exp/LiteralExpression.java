package main.ast.exp;

import main.ast.*;
import main.lexer.TokenType;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.List;

public final class LiteralExpression extends Expression {

    private final Object value;
    private final TokenType type;

    public LiteralExpression(Object value,
                             TokenType type,
                             Span span,
                             List<Comment> lead,
                             List<Comment> trail) {
        super(NodeKind.LITERAL, span, lead, trail);
        this.value = value;
        this.type = type;
    }

    public LiteralExpression(Object value,
                             TokenType type) {
        super(NodeKind.LITERAL);
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public List<Node> getChildren() {
        return List.of();
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitLiteral(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
