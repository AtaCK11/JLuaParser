package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.util.Comment;
import main.util.Span;

import java.util.List;

public abstract class Expression extends Node {

    protected Expression(NodeKind kind,
                         Span span,
                         List<Comment> lead,
                         List<Comment> trail) {
        super(kind, span, lead, trail);
    }

    protected Expression(NodeKind kind) {
        super(kind);
    }


}
