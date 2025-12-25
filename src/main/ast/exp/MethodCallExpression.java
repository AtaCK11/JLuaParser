package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;
import java.util.List;

public class MethodCallExpression extends Expression {
    private final Expression value;
    private final Expression methodName;
    private final List<Expression> arguments;

    public MethodCallExpression(Expression value,
                                Expression methodName,
                                List<Expression> arguments,
                                Span span, List<Comment> lead, List<Comment> trail) {
        super(NodeKind.METHOD_CALL, span, lead, trail);
        this.value = adoptChild(value);
        this.methodName = adoptChild(methodName);
        this.arguments = adoptChildren(arguments);
    }

    public MethodCallExpression(Expression value,
                                Expression methodName,
                                List<Expression> arguments) {
        super(NodeKind.METHOD_CALL);
        this.value = adoptChild(value);
        this.methodName = adoptChild(methodName);
        this.arguments = adoptChildren(arguments);
    }

    public Expression getValue() { return value; }
    public Expression getMethodName() { return methodName; }
    public List<Expression> getArguments() { return arguments; }

    @Override
    public List<Node> getChildren() {
        List<Node> children = new java.util.ArrayList<>();
        children.add(value);
        children.add(methodName);
        children.addAll(arguments);
        return children;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitMethodCall(this);
    }
}