package main.examples.rewriters;

import main.ast.Node;
import main.ast.exp.BinaryExpression;
import main.ast.exp.IdentifierExpression;
import main.ast.exp.LiteralExpression;
import main.ast.stmt.*;
import main.lexer.TokenType;
import main.visit.BaseRewriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Example: Flattens the specific body
 */
public class FlattenerRewriter extends BaseRewriter {

    private boolean shouldObfuscateNextBlock = false;

    @Override
    public Node visitIf(IfStatement n) {
        // 1. Check if the condition is: test == "flatten me"
        if (n.getExpression() instanceof BinaryExpression bin) {
            if (bin.getRight() instanceof LiteralExpression lit &&
                    lit.getValue().equals("flatten me")) {

                // 2. Directly flatten ONLY this specific body
                // We call flatten() here instead of relying on a flag
                Block obfuscatedBody = flatten(n.getBody());

                // Visit other branches normally
                List<ElseIfStatement> elseIfs = acceptList(n.getElseIfs());
                ElseStatement elseStmt = n.getElseStmt() != null ? (ElseStatement) n.getElseStmt().accept(this) : null;

                return new IfStatement(n.getExpression(), obfuscatedBody, elseIfs, elseStmt, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
            }
        }

        // If its not our target, use the standard BaseRewriter logic
        return super.visitIf(n);
    }

    /**
     * The flattening logic is now just a standalone utility within the rewriter.
     */
    private Block flatten(Block n) {
        List<Statement> original = n.getStatements();
        if (original.size() <= 1) return n;

        // Create the state variable and dispatcher as before
        IdentifierExpression stateVar = new IdentifierExpression("_state");
        LocalStatement initState = new LocalStatement(
                List.of(stateVar),
                List.of(new LiteralExpression(0, TokenType.NUMBER))
        );

        List<ElseIfStatement> elseIfs = new ArrayList<>();

        // First state
        Statement s0 = (Statement) original.get(0).accept(this);
        Block body0 = new Block(List.of(
                s0,
                new AssignmentStatement(List.of(stateVar), List.of(new LiteralExpression(1, TokenType.NUMBER)))
        ));

        // Remaining states
        for (int i = 1; i < original.size(); i++) {
            Statement sIdx = (Statement) original.get(i).accept(this);
            int nextState = (i == original.size() - 1) ? -1 : i + 1;

            Block bodyIdx = new Block(List.of(
                    sIdx,
                    new AssignmentStatement(List.of(stateVar), List.of(new LiteralExpression(nextState, TokenType.NUMBER)))
            ));

            elseIfs.add(new ElseIfStatement(
                    new BinaryExpression(stateVar, TokenType.EQUAL, new LiteralExpression(i, TokenType.NUMBER)),
                    bodyIdx
            ));
        }

        IfStatement dispatcher = new IfStatement(
                new BinaryExpression(stateVar, TokenType.EQUAL, new LiteralExpression(0, TokenType.NUMBER)),
                body0, elseIfs, null
        );

        WhileStatement loop = new WhileStatement(
                new BinaryExpression(stateVar, TokenType.NOT_EQUAL, new LiteralExpression(-1, TokenType.NUMBER)),
                new Block(List.of(dispatcher))
        );

        return new Block(List.of(initState, loop));
    }
}