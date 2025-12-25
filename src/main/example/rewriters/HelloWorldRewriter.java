package main.example.rewriters;

import main.ast.Node;
import main.ast.exp.LiteralExpression;
import main.lexer.TokenType;
import main.visit.BaseRewriter;

/**
 * Example: Overriding LiteralExpression to change string values
 */
public class HelloWorldRewriter extends BaseRewriter {

    @Override
    public Node visitLiteral(LiteralExpression n) {
        Object value = n.getValue();

        // "goodbye world" -> "hello world"
        if (value instanceof String && value.equals("goodbye world")) {
            return new LiteralExpression("hello world", TokenType.STRING);
        }

        return n;
    }
}