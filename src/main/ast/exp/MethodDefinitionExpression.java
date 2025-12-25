package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.List;

public class MethodDefinitionExpression extends Expression {
    private final Expression value;
    private final Expression methodName;

    public MethodDefinitionExpression(Expression value,
                                Expression methodName,
                                Span span, List<Comment> lead, List<Comment> trail) {
        super(NodeKind.METHOD_DEFINITION, span, lead, trail);
        this.value = adoptChild(value);
        this.methodName = adoptChild(methodName);
    }

    public MethodDefinitionExpression(Expression value,
                                Expression methodName) {
        super(NodeKind.METHOD_DEFINITION);
        this.value = adoptChild(value);
        this.methodName = adoptChild(methodName);
    }

    public Expression getValue() { return value; }
    public Expression getMethodName() { return methodName; }

    @Override
    public List<Node> getChildren() {
        List<Node> children = new java.util.ArrayList<>();
        children.add(value);
        children.add(methodName);
        return children;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitMethodDefinition(this);
    }
}