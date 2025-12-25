package main.util;

import main.ast.Chunk;
import main.ast.Node;
import main.visit.NodeVisitor;
import main.ast.exp.*;
import main.ast.stmt.*;
import main.lexer.TokenType;

import java.util.List;

/**
 * A code generation utility that implements {@link NodeVisitor} to transform an
 * AST back into Lua source code.
 * <p>This class performs "un-parsing," effectively acting as a formatter. It handles
 * indentation levels using tabs and supports optional semicolon injection for
 * statement termination. Can be used as a beautifier for the given lua source.</p>
 * <h3>Usage:</h3>
 * <pre>{@code
 * // Set addSemicolons to true for "return 1;" style output
 * String luaCode = ASTStringGenerator.generate(rootNode, false);
 * System.out.println(luaCode);
 * }</pre>
 */
public final class ASTStringGenerator implements NodeVisitor<Void> {

    private final StringBuilder out = new StringBuilder();
    private int indent = 0;
    private static boolean addSemicolons = false;

    // ─────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────

    /**
     * Entry point for generating Lua source code from an AST node.
     * @param root The root node of the AST to generate code for.
     * @param addSemicolonsToEnd If true, appends a {@code ;} to the end of statements.
     * @return A {@code String} containing the generated Lua source code.
     */
    public static String generate(Node root, boolean addSemicolonsToEnd) {
        addSemicolons = addSemicolonsToEnd;
        ASTStringGenerator pp = new ASTStringGenerator();
        root.accept(pp);
        return pp.out.toString();
    }

    private void append(String text) {
        out.append(text);
    }

    private void appendWithIndent(String text) {
        for (int i = 0; i < indent; i++) {
            out.append("\t");
        }
        out.append(text);
    }

    private void appendln(String text, boolean semicolons) {
        append(text);
        if (addSemicolons && semicolons) {
            out.append(";");
        }
        out.append("\n");
    }

    private void withIndent(Runnable r) {
        indent++;
        try {
            r.run();
        } finally {
            indent--;
        }
    }

    private String listToString(java.util.List<? extends Node> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString());
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // ─────────────────────────────────────────────
    // Root
    // ─────────────────────────────────────────────

    @Override
    public Void visitChunk(Chunk n) {
        n.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visitBlock(Block n) {
        for (Statement s : n.getStatements()) {
            s.accept(this);
        }
        return null;
    }

    @Override
    public Void visitAssignment(AssignmentStatement n) {
        var variables = n.getVariables();
        appendWithIndent("");
        for (Expression v : variables) {
            v.accept(this);
            append(", ");
        }
        if (variables.size() >= 1) {
            out.setLength(out.length() - 2);
        }
        append(" = ");
        var values = n.getValues();
        for (Expression v : values) {
            v.accept(this);
            append(", ");
        }
        if (values.size() >= 1) {
            out.setLength(out.length() - 2);
        }
        appendln("", true);
        return null;
    }

    @Override
    public Void visitLocal(LocalStatement n) {
        appendWithIndent("local " );
        var variables = n.getVariables();
        for (Expression v : variables) {
            v.accept(this);
            append(", ");
        }
        if (variables.size() >= 1) {
            out.setLength(out.length() - 2);
        }
        if (!n.getValues().isEmpty()) {
            append(" = ");
        }
        var values = n.getValues();
        for (Expression v : values) {
            v.accept(this);
            append(", ");
        }
        if (values.size() >= 1) {
            out.setLength(out.length() - 2);
        }
        appendln("", true);
        return null;
    }

    @Override
    public Void visitReturn(ReturnStatement n) {
        appendWithIndent("return");
        var values = n.getValues();
        if (!values.isEmpty()) {
            append(" ");
            for (Expression v : values) {
                v.accept(this);
            }
        }
        appendln("", true);
        return null;
    }

    @Override
    public Void visitBreak(BreakStatement n) {
        appendWithIndent("");
        append("break");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitContinue(ContinueStatement n) {
        appendWithIndent("");
        append("continue");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitDo(DoStatement n) {
        appendWithIndent("");
        append("do\n");
        withIndent(() -> {
            n.getBody().accept(this);
        });
        appendln("end", true);
        return null;
    }

    @Override
    public Void visitIf(IfStatement n) {
        appendWithIndent("if ");
        n.getExpression().accept(this);
        append(" then\n");
        withIndent(() -> {
            n.getBody().accept(this);
        });
        for (ElseIfStatement elseif : n.getElseIfs()) {
            elseif.accept(this);
        }
        if (n.getElseStmt() != null) {
            n.getElseStmt().accept(this);
        }
        appendWithIndent("end");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitElseIf(ElseIfStatement n) {
        appendWithIndent("elseif ");
        n.getExpression().accept(this);
        appendln(" then", false);
        withIndent(() -> {
            n.getBody().accept(this);
        });
        return null;
    }

    @Override
    public Void visitElse(ElseStatement n) {
        appendWithIndent("else");
        withIndent(() -> {
            n.getBody().accept(this);
        });
        return null;
    }

    @Override
    public Void visitWhile(WhileStatement n) {
        appendWithIndent("while ");
        n.getExpression().accept(this);
        append(" do\n");
        withIndent(() -> {
            n.getBody().accept(this);
        });
        appendWithIndent("end");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitRepeat(RepeatStatement n) {
        appendWithIndent("");
        appendln("repeat", false);
        withIndent(() -> {
            n.getBody().accept(this);
        });
        appendWithIndent("until ");
        n.getExpression().accept(this);
        appendln("", true);
        return null;
    }

    @Override
    public Void visitForNumeric(ForNumericStatement n) {
        appendWithIndent("for ");
        n.getVariable().accept(this);
        append(" = ");
        n.getStart().accept(this);
        append(", ");
        n.getEnd().accept(this);
        if (n.getStep() != null) {
            append(", ");
            n.getStep().accept(this);
        }
        appendln(" do", false);
        withIndent(() -> {
            for ( Statement s : n.getBody().getStatements()) {
                s.accept(this);
            }
        });
        appendWithIndent("end");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitForGeneric(ForGenericStatement n) {
        appendWithIndent("for ");
        List<IdentifierExpression> vars = n.getNames();
        for (Expression var : vars) {
            var.accept(this);
            append(", ");
        }
        if (!vars.isEmpty()) {
            out.setLength(out.length() - 2);
        }
        append(" in ");
        Expression iterator = n.getExpression();
        iterator.accept(this);

        appendln(" do", false);
        withIndent(() -> {
            n.getBody().accept(this);
        });
        appendWithIndent("end");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement n) {
        appendWithIndent("");
        n.getExpression().accept(this);
        appendln("", true);
        return null;
    }

    @Override
    public Void visitLocalFunction(LocalFunctionStatement n) {
        appendWithIndent("local function ");
        n.getName().accept(this);
        append("(");
        List<Expression> params = n.getParameters();
        for (Expression param : params) {
            param.accept(this);
            append(", ");
        }
        if (!params.isEmpty()) {
            out.setLength(out.length() - 2);
        }
        appendln(")", false);
        withIndent(() -> {
            n.getChunk().accept(this);
        });
        appendWithIndent("end");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitFunction(FunctionStatement n) {
        appendWithIndent("function ");
        n.getName().accept(this);
        append("(");
        List<Expression> params = n.getParameters();
        for (Expression param : params) {
            param.accept(this);
            append(", ");
        }
        if (!params.isEmpty()) {
            out.setLength(out.length() - 2);
        }
        appendln(")", false);
        withIndent(() -> {
            n.getChunk().accept(this);
        });
        appendWithIndent("end");
        appendln("", true);
        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierExpression n) {
        append(n.getName());
        return null;
    }

    @Override
    public Void visitLiteral(LiteralExpression n) {
        if (n.getType() == TokenType.STRING) {
            append("\"" + n.getValue() + "\"");
        } else {
            append(n.getValue().toString());
        }
        return null;
    }

    @Override
    public Void visitBinary(BinaryExpression n) {
        n.getLeft().accept(this);

        if (n.getOp().isKeyword()) {
            append(" " + n.getOp().getKeywordText() + " ");
        } else {
            append(" " + n.getOp().getSymbolText() + " ");
        }

        n.getRight().accept(this);

        return null;
    }

    @Override
    public Void visitUnary(UnaryExpression n) {
        if (n.getOp().isKeyword()) {
            append(n.getOp().getKeywordText() + " ");
        } else {
            append(n.getOp().getSymbolText());
        }
        n.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visitFunctionCall(FunctionCallExpression n) {
        n.getTarget().accept(this);
        append("(");
        List<Expression> args = n.getArgs();
        return appendArguments(args);
    }

    @Override
    public Void visitVarArg(VarArgExpression n) {
        append("...");
        return null;
    }

    @Override
    public Void visitTableConstructor(TableConstructorExpression n) {
        append("{ ");
        List<TableFieldExpression> fields = n.getFields();
        for (TableFieldExpression field : fields) {
            field.accept(this);
            append(", ");
        }
        if (!fields.isEmpty()) {
            out.setLength(out.length() - 2);
        }
        append(" }");
        return null;
    }

    @Override
    public Void visitTableField(TableFieldExpression n) {
        if (n.getKey() != null) {
            append("[");
            n.getKey().accept(this);
            append("]");
            append(" = ");
        } else {
            append("");
        }

        n.getValue().accept(this);
        return null;
    }

    @Override
    public Void visitTableAccess(TableAccessExpression n) {
        if (n.isDotAccess()) {
            n.getName().accept(this);
            append(".");
            n.getIndex().accept(this);
        } else {
            n.getName().accept(this);
            append("[");
            n.getIndex().accept(this);
            append("]");
        }
        return null;
    }

    @Override
    public Void visitAnonymousFunction(AnonymousFunctionExpression n) {
        append("function(");
        List<Expression> params = n.getParameters();
        for (Expression param : params) {
            param.accept(this);
            append(", ");
        }
        if (!params.isEmpty()) {
            out.setLength(out.length() - 2);
        }
        appendln(")", false);
        withIndent(() -> {
            n.getChunk().accept(this);
        });
        append("end");
        return null;
    }

    @Override
    public Void visitParanthesis(ParanthesisExpression paranthesisExpression) {
        append("(");
        paranthesisExpression.getInnerExpr().accept(this);
        append(")");
        return null;
    }

    @Override
    public Void visitMethodCall(MethodCallExpression methodCallExpression) {
        methodCallExpression.getValue().accept(this);
        append(":");
        methodCallExpression.getMethodName().accept(this);
        append("(");
        List<Expression> args = methodCallExpression.getArguments();
        return appendArguments(args);
    }

    @Override
    public Void visitMethodDefinition(MethodDefinitionExpression n) {
        n.getValue().accept(this);
        append(":");
        n.getMethodName().accept(this);
        return null;
    }

    private Void appendArguments(List<Expression> args) {
        for (Expression arg : args) {
            arg.accept(this);
            append(", ");
        }
        if (!args.isEmpty()) {
            out.setLength(out.length() - 2);
        }
        append(")");
        return null;
    }
}
