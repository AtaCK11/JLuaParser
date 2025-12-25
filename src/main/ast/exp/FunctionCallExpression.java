package main.ast.exp;

import main.ast.*;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public final class FunctionCallExpression extends Expression {

    private final Expression target;
    private final List<Expression> args;

    public FunctionCallExpression(Expression target,
                                  List<Expression> args,
                                  Span span,
                                  List<Comment> lead,
                                  List<Comment> trail) {
        super(NodeKind.FUNCTION_CALL, span, lead, trail);
        this.target = adoptChild(target);
        this.args = adoptAll(args);
    }

    public FunctionCallExpression(Expression target,
                                  List<Expression> args) {
        super(NodeKind.FUNCTION_CALL);
        this.target = adoptChild(target);
        this.args = adoptAll(args);
    }

    public Expression getTarget() {
        return target;
    }

    public List<Expression> getArgs() {
        return args;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.add(target);
        out.addAll(args);
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitFunctionCall(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(target.toString());
        sb.append("(");
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i).toString());
            if (i < args.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
