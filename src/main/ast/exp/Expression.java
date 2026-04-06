package main.ast.exp;

import main.ast.Node;
import main.ast.NodeKind;
import main.lexer.Token;
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

    protected Expression(NodeKind kind,
                         Token token,
                         Span span,
                         List<Comment> lead,
                         List<Comment> trail) {
        super(kind, token, span, lead, trail);
    }

    protected Expression(NodeKind kind) {
        super(kind);
    }

    protected Expression(NodeKind kind, Token token) {
        super(kind, token);
    }

}
