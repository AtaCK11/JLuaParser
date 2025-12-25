package main.ast.exp;

import main.ast.*;
import main.lexer.TokenType;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.List;

public final class BinaryExpression extends Expression {

    private final Expression left;
    private final TokenType op;
    private final Expression right;

    public BinaryExpression(Expression left,
                            TokenType op,
                            Expression right,
                            Span span,
                            List<Comment> lead,
                            List<Comment> trail) {
        super(NodeKind.BINARY, span, lead, trail);
        this.left = adoptChild(left);
        this.op   = op;
        this.right = adoptChild(right);
    }

    public BinaryExpression(Expression left,
                            TokenType op,
                            Expression right) {
        super(NodeKind.BINARY);
        this.left = adoptChild(left);
        this.op   = op;
        this.right = adoptChild(right);
    }

    public Expression getLeft() {
        return left;
    }

    public TokenType getOp() {
        return op;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public List<Node> getChildren() {
        return List.of(left, right);
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitBinary(this);
    }

    @Override
    public String toString() {
        return "(" + left + " " + op + " " + right + ")";
    }
}
