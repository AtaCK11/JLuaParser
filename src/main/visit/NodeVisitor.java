package main.visit;

import main.ast.Chunk;
import main.ast.exp.*;
import main.ast.stmt.*;
import main.ast.stmt.FunctionStatement;

public interface NodeVisitor<R> {

    // Root
    R visitChunk(Chunk n);

    // Statements
    R visitBlock(Block n);
    R visitAssignment(AssignmentStatement n);
    R visitLocal(LocalStatement n);
    R visitReturn(ReturnStatement n);
    R visitBreak(BreakStatement n);
    R visitContinue(ContinueStatement n);

    R visitDo(DoStatement n);

    // Control Flow Statements
    R visitIf(IfStatement n);
    R visitElseIf(ElseIfStatement n);
    R visitElse(ElseStatement n);
    R visitWhile(WhileStatement n);
    R visitRepeat(RepeatStatement n);
    R visitForNumeric(ForNumericStatement n);
    R visitForGeneric(ForGenericStatement n);

    // Expressions
    R visitExpressionStatement(ExpressionStatement n);
    R visitIdentifier(IdentifierExpression n);
    R visitLiteral(LiteralExpression n);
    R visitBinary(BinaryExpression n);
    R visitUnary(UnaryExpression n);
    R visitFunctionCall(FunctionCallExpression n);

    R visitVarArg(VarArgExpression n);

    R visitTableConstructor(TableConstructorExpression n);

    R visitTableField(TableFieldExpression n);

    R visitTableAccess(TableAccessExpression n);

    R visitLocalFunction(LocalFunctionStatement n);

    R visitFunction(FunctionStatement n);

    R visitAnonymousFunction(AnonymousFunctionExpression n);

    R visitParanthesis(ParanthesisExpression n);

    R visitMethodCall(MethodCallExpression n);

    R visitMethodDefinition(MethodDefinitionExpression n);

    // more as needed...
}