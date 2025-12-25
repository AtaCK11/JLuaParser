package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.visit.NodeVisitor;
import main.util.Comment;
import main.util.Span;

import java.util.ArrayList;
import java.util.List;

public final class TableConstructorExpression extends Expression {

    private final  List<TableFieldExpression> fields = new ArrayList<>();

    public TableConstructorExpression(List<TableFieldExpression> fields,
                                      Span span,
                                      List<Comment> lead,
                                      List<Comment> trail) {
        super(NodeKind.TABLE_CONSTRUCTOR, span, lead, trail);
        for (Expression field : fields) {
            this.fields.add((TableFieldExpression) adoptChild(field));
        }
    }

    public TableConstructorExpression(List<TableFieldExpression> fields) {
        super(NodeKind.TABLE_CONSTRUCTOR);
        for (Expression field : fields) {
            this.fields.add((TableFieldExpression) adoptChild(field));
        }
    }

    public List<TableFieldExpression> getFields() {
        return fields;
    }

    @Override
    public List<Node> getChildren() {
        return List.of();
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitTableConstructor(this);
    }

    @Override
    public String toString() {
        return "{" + String.join(", ", fields.stream().map(Object::toString).toList()) + "}";
    }
}
