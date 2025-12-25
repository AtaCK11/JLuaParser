package main.examples.rewriters;

import main.ast.Node;
import main.ast.exp.LiteralExpression;
import main.ast.stmt.*;
import main.lexer.TokenType;
import main.visit.BaseRewriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Example: Wraps all the Statements with if true then ... end
 */
public class IfWrapperRewriter extends BaseRewriter {

    private Statement wrapInIfTrue(Statement stmt) {
        // 'true' condition
        LiteralExpression trueCond = new LiteralExpression(true, TokenType.TRUE);

        // New body with our original statements
        List<Statement> body = List.of(stmt);

        // Return the new IfStatement with empty else/elseif branches
        return new IfStatement(
                trueCond,         // condition
                new Block(body),  // If body
                List.of(),        // Else if
                null              // Else
        );
    }

    @Override
    public Node visitBlock(Block n) {
        // Transform all statements in any block
        List<Statement> transformed = transformAll(n.getStatements());
        return new Block(transformed, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    /**
     * Iterates through a list of statements, visits them to allow inner
     * rewrites, and then wraps them.
     */
    private List<Statement> transformAll(List<Statement> original) {
        List<Statement> rewritten = new ArrayList<>();
        for (Statement s : original) {
            Statement innerProcessed = (Statement) s.accept(this);

            rewritten.add(wrapInIfTrue(innerProcessed));
        }
        return rewritten;
    }
}