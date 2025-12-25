package main.examples;

import main.ast.Chunk;
import main.examples.rewriters.FlattenerRewriter;
import main.parser.Parser;
import main.util.ASTStringGenerator;
import main.examples.rewriters.HelloWorldRewriter;
import main.examples.rewriters.IfWrapperRewriter;

public class RewriterExample {

    void main() {
        var willbeRewritten = """
                print(1)
                print(2)
                print(3)
                print(4)
                a = 1
                print("goodbye world")
                if test == "flatten me" then 
                print("Step 1")
                print("Step 2")
                print("Step 3")
                end
                
                if test == "dont wrap my body" then
                why_im_wrapped = -1
                end
                """;

        var ast = new Parser(willbeRewritten).parseChunk();

        // swap "goodbye world" with "hello world"
        var rewrittenAst = (Chunk) ast.accept(new HelloWorldRewriter());
        var rewrittenSource = ASTStringGenerator.generate(rewrittenAst, true);
        IO.println("\n\nRewritten Source:\n" + rewrittenSource);

        // Wrap every statement in if true then ... end
        var wrappedAst = (Chunk) rewrittenAst.accept(new IfWrapperRewriter());
        var wrappedSource = ASTStringGenerator.generate(wrappedAst, true);
        IO.println("\n\nIf Wrapped Source:\n" + wrappedSource);

        // Only flatten the selected block
        var flattener = new FlattenerRewriter();
        var obfuscatedAST = (Chunk) rewrittenAst.accept(flattener);
        var obfuscatedSource = ASTStringGenerator.generate(obfuscatedAST, true);
        IO.println("\n\nObfuscated Source:\n" + obfuscatedSource);
    }

}
