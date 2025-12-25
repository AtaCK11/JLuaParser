package main.examples;

import main.parser.Parser;
import main.util.ASTStringGenerator;

public class BeautifyExample {

    void main() {
        var luaSource = """
                local count = 0
                for a = 1, 2 do                 -- 1
                for b = 1, 2 do                 -- 2
                local c = 0
                while c < 2 do                  -- 3
                c = c + 1
                for d = 1, 2 do                 -- 4
                local e = 0
                repeat-- 5
                e = e + 1
                for f = 1, 2 do                 -- 6
                local g = 0
                while g < 2 do                  -- 7
                g = g + 1
                for h = 1, 2 do                 -- 8
                count = count + 1
                end
                end
                end
                until e >= 2
                end
                end
                end
                end
                
                print("Inner body executed:", count, "times")
                
                """;

        var ast = new Parser(luaSource).parseChunk();

        var b = ASTStringGenerator.generate(ast, true);
        IO.println(b);
    }

}
