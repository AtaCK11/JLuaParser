package main.examples;

import main.ast.exp.Expression;
import main.ast.exp.FunctionCallExpression;
import main.ast.exp.IdentifierExpression;
import main.ast.exp.LiteralExpression;
import main.ast.stmt.Block;
import main.ast.stmt.ExpressionStatement;
import main.ast.stmt.ForNumericStatement;
import main.lexer.TokenType;
import main.util.ASTStringGenerator;

import java.util.List;

public class GenerateASTExample {

    void main() {
        //Create loop variable 'i'
        IdentifierExpression varI = new IdentifierExpression("i");

        // Create start (1), end (10), step (1)
        LiteralExpression start = new LiteralExpression(1, TokenType.NUMBER);
        LiteralExpression end = new LiteralExpression(10, TokenType.NUMBER);
        LiteralExpression step = new LiteralExpression(1, TokenType.NUMBER);

        // Create body print(i)
        List<Expression> args = List.of(varI);
        FunctionCallExpression call = new FunctionCallExpression(new IdentifierExpression("print"), args);
        Block body = new Block(List.of(new ExpressionStatement(call)));

        // Finalize the ForNumericStatement
        ForNumericStatement forLoop = new ForNumericStatement(
                varI,
                start,
                end,
                step,
                body
        );

        var astSource = ASTStringGenerator.generate(forLoop, true);
        IO.println(astSource);
    }
}
