package main.util;

public final class Comment {
    public enum CommentKind { LINE, BLOCK }

    private final CommentKind kind;
    private final String text;
    private final Span span;

    public Comment(CommentKind kind, String text, Span span) {
        this.kind = kind;
        this.text = text;
        this.span = span;
    }

    public CommentKind getKind() { return kind; }
    public String getText()      { return text; }
    public Span getSpan()        { return span; }
}