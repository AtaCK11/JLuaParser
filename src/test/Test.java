package test;

import main.lexer.Lexer;
import main.parser.exceptions.ParseException;
import main.parser.Parser;
import main.util.ASTStringGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Test {

    void main() {

        List<String> testPaths = new ArrayList<>();
        testPaths.add("src/test/statements/AssignmentStatement.lua");
        testPaths.add("src/test/statements/BreakStatement.lua");
        testPaths.add("src/test/statements/ContinueStatement.lua");
        testPaths.add("src/test/statements/DoStatement.lua");
        testPaths.add("src/test/statements/ElseIfStatement.lua");
        testPaths.add("src/test/statements/ElseStatement.lua");
        testPaths.add("src/test/statements/ExpressionStatement.lua");
        testPaths.add("src/test/statements/ForGenericStatement.lua");
        testPaths.add("src/test/statements/ForNumericStatement.lua");
        testPaths.add("src/test/statements/FunctionStatement.lua");
        testPaths.add("src/test/statements/IfStatement.lua");
        testPaths.add("src/test/statements/LocalFunctionStatement.lua");
        testPaths.add("src/test/statements/LocalStatement.lua");
        testPaths.add("src/test/statements/RepeatStatement.lua");
        testPaths.add("src/test/statements/ReturnStatement.lua");
        testPaths.add("src/test/statements/WhileStatement.lua");

        System.out.println("Current folder:");
        System.out.println(System.getProperty("user.dir"));


        String currentTestPath = "";


        for (String testPath : testPaths) {
            // eliminate src/test/statements/
            currentTestPath = testPath.replace("src/test/statements/", "");
            try {
                int startTime = (int) System.currentTimeMillis();

                var path = Path.of(testPath); // relative path
                var luaSource = Files.readString(path);

                var lexer = new Lexer(luaSource);
                var tokens = lexer.tokenize();

                var parser = new Parser(tokens);
                var ast = parser.parseChunk();

                var b = ASTStringGenerator.generate(ast, true);

                var endTime = (int) System.currentTimeMillis();
                var duration = endTime - startTime;

                IO.println("[OK] Parsed -> " + testPath + "Time: " + duration + " ms");
            } catch (IOException e) {
                IO.println("IO Error in file: " + currentTestPath);
                IO.println(e.getMessage());
            } catch (ParseException pe) {
                IO.print("[ERROR] Parse Error in file: " + currentTestPath + " ");
                IO.println(pe.getMessage());
            }

        }

    }
}