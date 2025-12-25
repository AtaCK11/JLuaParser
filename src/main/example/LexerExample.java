package main.example;

import main.lexer.Lexer;
import main.lexer.Token;
import main.parser.Parser;
import main.util.ASTStringGenerator;

public class LexerExample {

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

        for (Token t : tokens) {
            IO.println(t);
        }


    }

}
