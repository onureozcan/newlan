package org.zero.newlan.fe.ast.expression;

import org.zero.newlan.fe.operator.OperationNotSupportedException;
import org.zero.newlan.fe.operator.Operator;
import org.zero.newlan.fe.type.Type;

public class BinaryExpression extends Expression {

    private Expression left;

    private Expression right;

    private Operator operator;

    private BinaryExpression(
        Type type,
        String fileName,
        int lineNumber,
        int pos,
        Expression left,
        Expression right,
        Operator operator
    ) {
        super(type, fileName, lineNumber, pos);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Operator getOperator() {
        return operator;
    }

    static BinaryExpression from(Expression left, Expression right, Operator operator) throws OperationNotSupportedException {
        Type returnType = operator.returnsTo(left.getType(), right.getType());
        return new BinaryExpression(returnType, left.getFileName(), left.getLineNumber(), left.getPos(), left, right, operator);
    }

    @Override
    public String toString() {
        return "((" + this.left + " " + this.operator + " " + this.right + "): " + this.getType().getName() + ")";
    }
}
