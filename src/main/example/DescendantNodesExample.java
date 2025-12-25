package main.example;

import main.ast.Node;
import main.ast.exp.LiteralExpression;
import main.lexer.Lexer;
import main.parser.Parser;

public class DescendantNodesExample {

    void main() {
        var luaSource = """
                print("cccc")
                print("aaaa")
                print("bbbb")
                print("cccc")
                print("aaaa")
                print("bbbb")
                print("aaaa")
                print("cccc")
                print("aaaa")
                print("aaaa")
                
                """;

        // Parse using lua source
        var ast = new Parser(luaSource).parseChunk();

        for (Node n : ast.getDescendants()) {
            if (n instanceof LiteralExpression) {
                IO.println(n);
            }
        }

        IO.println("\n---------------\n");

        // You can achieve the same thing with
        for (Node n : ast.getDescendantsOfType(LiteralExpression.class)) {
            IO.println(n);
        }


    }

}
