package main.example;

import main.ast.Node;
import main.ast.exp.LiteralExpression;
import main.lexer.Lexer;
import main.parser.Parser;
import main.util.ASTPrettyPrinter;

public class ParserExample {

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

        var tokens = new Lexer(luaSource).tokenize();

        // Parse using tokens
        var ast = new Parser(tokens).parseChunk();

        var prettyPrint = ASTPrettyPrinter.generate(ast);

        IO.println(prettyPrint);


    }

}
