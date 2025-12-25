package main.ast.stmt;

import main.ast.Node;
import main.ast.NodeKind;
import main.util.Comment;
import main.util.Span;

import java.util.List;


public abstract class Statement extends Node {

    protected Statement(NodeKind kind,
                        Span span,
                        List<Comment> leading,
                        List<Comment> trailing) {
        super(kind, span, leading, trailing);
    }

    protected Statement(NodeKind kind) {
        super(kind);
    }
}
