package main.ast.stmt;

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
 * if <Expression> then
 *     <Block>
 * end
 * }</pre>
 */
public class IfStatement extends Statement {

    private final Expression expression;
    private final Block body;
    private final List<ElseIfStatement> elseIfs = new ArrayList<>();
    private ElseStatement elseStmt = null;

    public IfStatement(Expression expression,
                       Block body,
                       List<ElseIfStatement> elseIfs,
                       ElseStatement elseStmt,
                       Span span,
                       List<Comment> lead,
                       List<Comment> trail) {
        super(NodeKind.IF_STATEMENT, span, lead, trail);
        this.body = adoptChild(body);
        this.expression = adoptChild(expression);
        this.elseIfs.addAll(adoptAll(elseIfs));
        if (elseStmt != null) {
            this.elseStmt = elseStmt;
            adoptChild(elseStmt);
        }
    }

    public IfStatement(Expression expression,
                       Block body,
                       List<ElseIfStatement> elseIfs,
                       ElseStatement elseStmt) {
        super(NodeKind.IF_STATEMENT);
        this.body = adoptChild(body);
        this.expression = adoptChild(expression);
        this.elseIfs.addAll(adoptAll(elseIfs));
        if (elseStmt != null) {
            this.elseStmt = elseStmt;
            adoptChild(elseStmt);
        }
    }

    public Expression getExpression() {
        return expression;
    }

    public Block getBody() {
        return body;
    }

    public List<ElseIfStatement> getElseIfs() {
        return elseIfs;
    }

    public ElseStatement getElseStmt() {
        return elseStmt;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.add(expression);
        for (ElseIfStatement elseif : elseIfs) {
            out.add(elseif);
        }
        if (elseStmt != null) {
            out.add(elseStmt);
        }
        out.add(body);
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitIf(this);
    }

}
