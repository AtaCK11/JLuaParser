package main.ast;

import main.ast.stmt.Block;
import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public final class Chunk extends Node {

    private final Block block;
    public Chunk(Block statements,
                 Span span,
                 List<Comment> leading,
                 List<Comment> trailing) {
        super(NodeKind.CHUNK, span, leading, trailing);
        this.block = adoptChild(statements);
    }

    public Chunk(Block statements) {
        super(NodeKind.CHUNK);
        this.block = adoptChild(statements);
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> out = new ArrayList<>();
        out.addAll(block.getStatements());
        return out;
    }

    @Override
    public <R> R accept(NodeVisitor<R> nodeVisitor) {
        return nodeVisitor.visitChunk(this);
    }
}
