package main.util;

public final class Span {
    private final int startOffset;
    private final int endOffset;
    private final Position start;
    private final Position end;

    public Span(int startOffset, int endOffset, Position start, Position end) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.start = start;
        this.end = end;
    }

    public Span() {
        this.startOffset = 0;
        this.endOffset = 0;
        this.start = new Position();
        this.end = new Position();
    }

    public int getStartOffset() { return startOffset; }
    public int getEndOffset()   { return endOffset; }
    public Position getStart()  { return start; }
    public Position getEnd()    { return end; }
}