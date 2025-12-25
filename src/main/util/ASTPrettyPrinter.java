package main.util;

import main.ast.*;
import main.ast.stmt.*;
import main.ast.exp.*;
import main.ast.stmt.FunctionStatement;
import main.visit.NodeVisitor;

import java.util.List;

/**
 * A diagnostic utility that implements the {@link NodeVisitor} pattern to generate
 * a human-readable, tree-like string representation of the Abstract Syntax Tree.
 * <p>Visualizes nesting levels, making it easier to debug parser precedence and
 * statement scoping.</p>
 * * <h3>Usage Example:</h3>
 * <pre>{@code
 * String tree = ASTPrettyPrinter.generate(root);
 * System.out.println(tree);
 * }</pre>

 */
public final class ASTPrettyPrinter implements NodeVisitor<Void> {

    private final StringBuilder out = new StringBuilder();
    private int indent = 0;

    // ─────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────

    /**
     * Entry point for the AST printer.
     * <p>Traverses the provided AST starting from {@code root} and builds a formatted
     * string.</p>
     *
     * @param root The AST node to begin printing from.
     * @return A {@code String} containing the visual representation of the tree structure.
     */
    public static String generate(Node root) {
        ASTPrettyPrinter pp = new ASTPrettyPrinter();
        root.accept(pp);
        return pp.out.toString();
    }

    private void print(String text) {
        for (int i = 0; i < indent; i++) {
            out.append("|  "); // 2 spaces per level
        }
        out.append(text);
    }

    // New helper to handle the tree-style prefixing
    private void println(String text) {
        if (indent > 0) {
            for (int i = 0; i < indent - 1; i++) {
                out.append("│ ");
            }
            out.append("├──");
        }
        out.append(text).append('\n');
    }

    private void withIndent(Runnable r) {
        indent++;
        try {
            r.run();
        } finally {
            indent--;
        }
    }

    // ─────────────────────────────────────────────
    // Root
    // ─────────────────────────────────────────────

    @Override
    public Void visitChunk(Chunk n) {
        println("Chunk");
        withIndent(() -> {
            n.getBlock().accept(this);
        });
        return null;
    }

    // ─────────────────────────────────────────────
    // Statements
    // ─────────────────────────────────────────────

    @Override
    public Void visitBlock(Block n) {
        println("Block");
        withIndent(() -> {
            for (Statement s : n.getStatements()) {
                s.accept(this);
            }
        });
        return null;
    }

    @Override
    public Void visitAssignment(AssignmentStatement n) {
        println("AssignmentStatement");
        withIndent(() -> {
            List<Expression> vars = n.getVariables();
            List<Expression> vals = n.getValues();

            println("Left:");
            withIndent(() -> {
                for (Expression e : vars) {
                    e.accept(this);
                }
            });

            println("Right`:");
            withIndent(() -> {
                for (Expression e : vals) {
                    e.accept(this);
                }
            });
        });
        return null;
    }

    @Override
    public Void visitLocal(LocalStatement n) {
        println("LocalStatement");
        print("Names: ");
        for (var names : n.getVariables()) {
            out.append(names.getName()).append(" ");
        }
        out.append('\n');
        withIndent(() -> {
            if (!n.getValues().isEmpty()) {
                println("Values:");
                withIndent(() -> {
                    for (Expression e : n.getValues()) {
                        e.accept(this);
                    }
                });
            }
        });
        return null;
    }

    @Override
    public Void visitReturn(ReturnStatement n) {
        println("ReturnStatement");
        withIndent(() -> {
            for (Expression e : n.getValues()) {
                e.accept(this);
            }
        });
        return null;
    }

    @Override
    public Void visitBreak(BreakStatement n) {
        println("BreakStatement");
        return null;
    }

    @Override
    public Void visitContinue(ContinueStatement n) {
        println("visitContinue");
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement n) {
        println("ExpressionStatement");
        withIndent(() -> n.getExpression().accept(this));
        return null;
    }

    @Override
    public Void visitDo(DoStatement n) {
        println("DoStatement");
        withIndent(() -> {
            n.getBody().accept(this);
        });
        return null;
    }

    @Override
    public Void visitIf(IfStatement n) {
        println("IfStatement");
        withIndent(() -> {
            println("Condition:");
            withIndent(() -> n.getExpression().accept(this));
            println("Body:");
            withIndent(() -> {
                n.getBody().accept(this);
                if (!n.getElseIfs().isEmpty()) {
                    println("ElseIfs:");
                    withIndent(() -> {
                        for (ElseIfStatement elseif : n.getElseIfs()) {
                            elseif.accept(this);
                        }
                    });
                }
                if (n.getElseStmt() != null) {
                    println("Else:");
                    withIndent(() -> n.getElseStmt().accept(this));
                }
            });
        });
        return null;
    }

    @Override
    public Void visitElseIf(ElseIfStatement n) {
        println("ElseIfStatement");
        withIndent(() -> {
            println("Condition:");
            withIndent(() -> n.getExpression().accept(this));
            println("Body:");
            withIndent(() -> {
                n.getBody().accept(this);
            });
        });
        return null;
    }

    @Override
    public Void visitElse(ElseStatement n) {
        println("ElseStatement");
        println("Body:");
        withIndent(() -> {
            n.getBody().accept(this);
        });
        return null;
    }

    @Override
    public Void visitWhile(WhileStatement n) {
        println("WhileStatement");
        withIndent(() -> {
            println("Condition:");
            withIndent(() -> n.getExpression().accept(this));
            println("Body:");
            withIndent(() -> {
                n.getBody().accept(this);
            });
        });
        return null;
    }

    @Override
    public Void visitRepeat(RepeatStatement n) {
        println("RepeatStatement");
        withIndent(() -> {
            println("Body:");
            withIndent(() -> {
                n.getBody().accept(this);
            });
            println("Condition:");
            withIndent(() -> n.getExpression().accept(this));
        });
        return null;
    }

    @Override
    public Void visitForNumeric(ForNumericStatement n) {
        println("ForNumericStatement name: " + n.getVariable().getName());
        withIndent(() -> {
            println("Start:");
            withIndent(() -> n.getStart().accept(this));

            println("End:");
            withIndent(() -> n.getEnd().accept(this));

            if (n.getStep() != null) {
                println("Step:");
                withIndent(() -> n.getStep().accept(this));
            }

            println("Body:");
            withIndent(() -> {
                for (Statement s : n.getBody().getStatements()) {
                    s.accept(this);
                }
            });
        });
        return null;
    }

    @Override
    public Void visitForGeneric(ForGenericStatement n) {
        print("ForGenericStatement names: ");
        for (var names : n.getNames()) {
            out.append(names.getName()).append(" ");
        }
        out.append('\n');
        withIndent(() -> {
            println("Expression:");
            withIndent(() -> n.getExpression().accept(this));

            println("Body:");
            withIndent(() -> {
                n.getBody().accept(this);
            });
        });
        return null;
    }

    @Override
    public Void visitLocalFunction(LocalFunctionStatement n) {
        print("LocalFunctionStatement name: ");
        n.getName().accept(this);
        // parameters
        print("Parameters: ");
        for (var param : n.getParameters()) {
            out.append(param).append(" ");
        }
        out.append('\n');
        withIndent(() -> {
            println("Chunk:");
            withIndent(() -> n.getChunk().accept(this));
        });
        return null;
    }

    @Override
    public Void visitFunction(FunctionStatement n) {
        println("FunctionStatement name: ");
        n.getName().accept(this);
        // parameters
        print("Parameters: ");
        for (var param : n.getParameters()) {
            out.append(param).append(" ");
        }
        out.append('\n');
        withIndent(() -> {
            println("Chunk:");
            withIndent(() -> n.getChunk().accept(this));
        });
        return null;
    }


    // ─────────────────────────────────────────────
    // Expressions
    // ─────────────────────────────────────────────

    @Override
    public Void visitIdentifier(IdentifierExpression n) {
        println("Identifier \"" + n.getName() + "\"");
        return null;
    }

    @Override
    public Void visitLiteral(LiteralExpression n) {
        println("Literal " + n.getValue());
        return null;
    }

    @Override
    public Void visitBinary(BinaryExpression n) {
        println("BinaryExpression op: " + n.getOp());
        withIndent(() -> {
            println("Left:");
            withIndent(() -> n.getLeft().accept(this));

            println("Right:");
            withIndent(() -> n.getRight().accept(this));
        });
        return null;
    }

    @Override
    public Void visitUnary(UnaryExpression n) {
        println("UnaryExpression op: " + n.getOp());
        withIndent(() -> n.getExpr().accept(this));
        return null;
    }

    @Override
    public Void visitFunctionCall(FunctionCallExpression n) {
        println("FunctionCall");
        withIndent(() -> {
            println("Target:");
            withIndent(() -> n.getTarget().accept(this));

            if (!n.getArgs().isEmpty()) {
                println("Args:");
                withIndent(() -> {
                    for (Expression e : n.getArgs()) {
                        e.accept(this);
                    }
                });
            }
        });
        return null;
    }

    @Override
    public Void visitVarArg(VarArgExpression n) {
        println("VarArgExpression");
        return null;
    }

    @Override
    public Void visitTableConstructor(TableConstructorExpression n) {
        println("TableConstructorExpression");
        withIndent(() -> {
            for (TableFieldExpression field : n.getFields()) {
                field.accept(this);
            }
        });
        return null;
    }

    @Override
    public Void visitTableField(TableFieldExpression n) {
        println("TableFieldExpression");
        withIndent(() -> {
            if (n.getKey() != null) {
                println("Key:");
                withIndent(() -> n.getKey().accept(this));
            }
            println("Value:");
            withIndent(() -> n.getValue().accept(this));
        });
        return null;
    }

    @Override
    public Void visitTableAccess(TableAccessExpression n) {
        println("TableAccessExpression");
        withIndent(() -> {
            println("Name:");
            withIndent(() -> n.getName().accept(this));
            println("Index:");
            withIndent(() -> n.getIndex().accept(this));
        });
        return null;
    }

    @Override
    public Void visitAnonymousFunction(AnonymousFunctionExpression n) {
        println("AnonymousFunctionExpression");
        print("Parameters: ");
        for (var param : n.getParameters()) {
            out.append(param).append(" ");
        }
        out.append('\n');
        withIndent(() -> {
            println("Chunk:");
            withIndent(() -> n.getChunk().accept(this));
        });
        return null;
    }

    @Override
    public Void visitParanthesis(ParanthesisExpression n) {
        println("ParanthesisExpression");
        withIndent(() -> n.getInnerExpr().accept(this));
        return null;
    }

    @Override
    public Void visitMethodCall(MethodCallExpression n) {
        println("MethodCallExpression method: " + n.getValue());
        withIndent(() -> {
            println("Target:");
            withIndent(() -> n.getMethodName().accept(this));
            if (!n.getArguments().isEmpty()) {
                println("Args:");
                withIndent(() -> {
                    for (Expression e : n.getArguments()) {
                        e.accept(this);
                    }
                });
            }
        });
        return null;
    }

    @Override
    public Void visitMethodDefinition(MethodDefinitionExpression n) {
        println("MethodDefinitionExpression method: " + n.getMethodName());
        withIndent(() -> {
            println("Value:");
            withIndent(() -> n.getValue().accept(this));
        });
        return null;
    }

}

