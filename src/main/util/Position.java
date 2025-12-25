package main.util;

public final class Position {
    private final int line;   // 1-based
    private final int column; // 1-based

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public Position() {
        this.line = 0;
        this.column = 0;
    }

    public int getLine()   { return line; }
    public int getColumn() { return column; }
}
