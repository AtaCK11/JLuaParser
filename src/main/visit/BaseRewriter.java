package main.visit;

import main.ast.Chunk;
import main.ast.Node;
import main.ast.exp.*;
import main.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A base implementation of a Rewriting Visitor for the Lua AST.
 * This visitor recursively traverses the tree and reconstructs parent nodes
 * only if their children are modified.
 */
public abstract class BaseRewriter implements NodeVisitor<Node> {

    // ─────────────────────────────────────────────
    // Helper: Manual List Rewriting
    // ─────────────────────────────────────────────

    /**
     * Internal helper to handle list identity. Returns the same list if
     * no elements changed to avoid unnecessary allocations.
     */
    @SuppressWarnings("unchecked")
    protected <T extends Node> List<T> acceptList(List<T> nodes) {
        if (nodes == null) return null;
        if (nodes.isEmpty()) return nodes;

        boolean changed = false;
        List<T> newNodes = new ArrayList<>(nodes.size());

        for (T node : nodes) {
            T newNode = (T) node.accept(this);
            if (newNode != node) changed = true;
            newNodes.add(newNode);
        }

        return changed ? newNodes : nodes;
    }

    // ─────────────────────────────────────────────
    // Root & Blocks
    // ─────────────────────────────────────────────

    @Override
    public Node visitChunk(Chunk n) {
        Block rewrittenBlock = (Block) n.getBlock().accept(this);
        if (rewrittenBlock == n.getBlock()) return n;
        return new Chunk(rewrittenBlock, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitBlock(Block n) {
        List<Statement> stmts = acceptList(n.getStatements());
        if (stmts == n.getStatements()) return n;
        return new Block(stmts, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    // ─────────────────────────────────────────────
    // Statements
    // ─────────────────────────────────────────────

    @Override
    public Node visitAssignment(AssignmentStatement n) {
        List<Expression> vars = acceptList(n.getVariables());
        List<Expression> vals = acceptList(n.getValues());
        if (vars == n.getVariables() && vals == n.getValues()) return n;
        return new AssignmentStatement(vars, vals, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitLocal(LocalStatement n) {
        @SuppressWarnings("unchecked")
        List<IdentifierExpression> vars = acceptList(n.getVariables());
        List<Expression> vals = acceptList(n.getValues());
        if (vars == n.getVariables() && vals == n.getValues()) return n;
        return new LocalStatement(vars, vals, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitReturn(ReturnStatement n) {
        List<Expression> vals = acceptList(n.getValues());
        if (vals == n.getValues()) return n;
        return new ReturnStatement(vals, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitExpressionStatement(ExpressionStatement n) {
        Expression expr = (Expression) n.getExpression().accept(this);
        if (expr == n.getExpression()) return n;
        return new ExpressionStatement(expr, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitDo(DoStatement n) {
        Block body = (Block) n.getBody().accept(this);
        if (body == n.getBody()) return n;
        return new DoStatement(body, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitWhile(WhileStatement n) {
        Expression cond = (Expression) n.getExpression().accept(this);
        Block body = (Block) n.getBody().accept(this);
        if (cond == n.getExpression() && body == n.getBody()) return n;
        return new WhileStatement(cond, body, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitRepeat(RepeatStatement n) {
        Block body = (Block) n.getBody().accept(this);
        Expression cond = (Expression) n.getExpression().accept(this);
        if (body == n.getBody() && cond == n.getExpression()) return n;
        return new RepeatStatement(cond, body, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitIf(IfStatement n) {
        Expression cond = (Expression) n.getExpression().accept(this);
        Block body = (Block) n.getBody().accept(this);
        List<ElseIfStatement> elseIfs = acceptList(n.getElseIfs());

        ElseStatement elseStmt = n.getElseStmt();
        if (elseStmt != null) {
            elseStmt = (ElseStatement) elseStmt.accept(this);
        }

        if (cond == n.getExpression() && body == n.getBody() &&
                elseIfs == n.getElseIfs() && elseStmt == n.getElseStmt()) return n;

        return new IfStatement(cond, body, elseIfs, elseStmt, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitElseIf(ElseIfStatement n) {
        Expression cond = (Expression) n.getExpression().accept(this);
        Block body = (Block) n.getBody().accept(this);
        if (cond == n.getExpression() && body == n.getBody()) return n;
        return new ElseIfStatement(cond, body, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitElse(ElseStatement n) {
        Block body = (Block) n.getBody().accept(this);
        if (body == n.getBody()) return n;
        return new ElseStatement(body, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitForNumeric(ForNumericStatement n) {
        IdentifierExpression var = (IdentifierExpression) n.getVariable().accept(this);
        Expression start = (Expression) n.getStart().accept(this);
        Expression end = (Expression) n.getEnd().accept(this);
        Expression step = n.getStep() != null ? (Expression) n.getStep().accept(this) : null;
        Block body = (Block) n.getBody().accept(this);

        if (var == n.getVariable() && start == n.getStart() && end == n.getEnd() &&
                step == n.getStep() && body == n.getBody()) return n;

        return new ForNumericStatement(var, start, end, step, body, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitForGeneric(ForGenericStatement n) {
        @SuppressWarnings("unchecked")
        List<IdentifierExpression> names = acceptList(n.getNames());
        Expression expr = (Expression) n.getExpression().accept(this);
        Block body = (Block) n.getBody().accept(this);

        if (names == n.getNames() && expr == n.getExpression() && body == n.getBody()) return n;

        return new ForGenericStatement(names, expr, body, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitLocalFunction(LocalFunctionStatement n) {
        IdentifierExpression name = (IdentifierExpression) n.getName().accept(this);
        List<Expression> params = acceptList(n.getParameters());
        Chunk chunk = (Chunk) n.getChunk().accept(this);

        if (name == n.getName() && params == n.getParameters() && chunk == n.getChunk()) return n;

        return new LocalFunctionStatement(name, params, chunk, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitFunction(FunctionStatement n) {
        Expression name = (Expression) n.getName().accept(this);
        List<Expression> params = acceptList(n.getParameters());
        Chunk chunk = (Chunk) n.getChunk().accept(this);

        if (name == n.getName() && params == n.getParameters() && chunk == n.getChunk()) return n;

        return new FunctionStatement(name, params, chunk, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    // ─────────────────────────────────────────────
    // Expressions
    // ─────────────────────────────────────────────

    @Override
    public Node visitBinary(BinaryExpression n) {
        Expression left = (Expression) n.getLeft().accept(this);
        Expression right = (Expression) n.getRight().accept(this);
        if (left == n.getLeft() && right == n.getRight()) return n;
        return new BinaryExpression(left, n.getOp(), right, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitUnary(UnaryExpression n) {
        Expression expr = (Expression) n.getExpr().accept(this);
        if (expr == n.getExpr()) return n;
        return new UnaryExpression(n.getOp(), expr, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitFunctionCall(FunctionCallExpression n) {
        Expression target = (Expression) n.getTarget().accept(this);
        List<Expression> args = acceptList(n.getArgs());
        if (target == n.getTarget() && args == n.getArgs()) return n;
        return new FunctionCallExpression(target, args, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitTableConstructor(TableConstructorExpression n) {
        List<TableFieldExpression> fields = acceptList(n.getFields());
        if (fields == n.getFields()) return n;
        return new TableConstructorExpression(fields, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitTableField(TableFieldExpression n) {
        Expression key = n.getKey() != null ? (Expression) n.getKey().accept(this) : null;
        Expression value = (Expression) n.getValue().accept(this);
        if (key == n.getKey() && value == n.getValue()) return n;
        return new TableFieldExpression(key, value, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitTableAccess(TableAccessExpression n) {
        Expression name = (Expression) n.getName().accept(this);
        Expression index = (Expression) n.getIndex().accept(this);
        if (name == n.getName() && index == n.getIndex()) return n;
        return new TableAccessExpression(name, index, n.isDotAccess(), n.isBracketAccess(), n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitAnonymousFunction(AnonymousFunctionExpression n) {
        List<Expression> params = acceptList(n.getParameters());
        Chunk chunk = (Chunk) n.getChunk().accept(this);
        if (params == n.getParameters() && chunk == n.getChunk()) return n;
        return new AnonymousFunctionExpression(params, chunk, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitParanthesis(ParanthesisExpression n) {
        Expression inner = (Expression) n.getInnerExpr().accept(this);
        if (inner == n.getInnerExpr()) return n;
        return new ParanthesisExpression(inner, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitMethodCall(MethodCallExpression n) {
        Expression target = (Expression) n.getValue().accept(this);
        IdentifierExpression method = (IdentifierExpression) n.getMethodName().accept(this);
        List<Expression> args = acceptList(n.getArguments());
        if (target == n.getValue() && method == n.getMethodName() && args == n.getArguments()) return n;
        return new MethodCallExpression(target, method, args, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    @Override
    public Node visitMethodDefinition(MethodDefinitionExpression n) {
        Expression target = (Expression) n.getValue().accept(this);
        IdentifierExpression method = (IdentifierExpression) n.getMethodName().accept(this);
        if (target == n.getValue() && method == n.getMethodName()) return n;
        return new MethodDefinitionExpression(target, method, n.getSpan(), n.getLeadingComments(), n.getTrailingComments());
    }

    // ─────────────────────────────────────────────
    // Leaf Nodes (Override to modify values)
    // ─────────────────────────────────────────────

    @Override public Node visitLiteral(LiteralExpression n) { return n; }
    @Override public Node visitIdentifier(IdentifierExpression n) { return n; }
    @Override public Node visitBreak(BreakStatement n) { return n; }
    @Override public Node visitContinue(ContinueStatement n) { return n; }
    @Override public Node visitVarArg(VarArgExpression n) { return n; }
}