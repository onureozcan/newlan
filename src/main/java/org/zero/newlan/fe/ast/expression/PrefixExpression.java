package org.zero.newlan.fe.ast.expression;

import org.zero.newlan.fe.operator.OperationNotSupportedException;
import org.zero.newlan.fe.operator.Operator;
import org.zero.newlan.fe.operator.PrefixOperator;
import org.zero.newlan.fe.type.ObjectType;
import org.zero.newlan.fe.type.Type;

public class PrefixExpression extends Expression {

    private Expression right;

    private PrefixOperator operator;

    private PrefixExpression(
        ObjectType thisObj,
        Type type,
        String fileName,
        int lineNumber,
        int pos,
        Expression right,
        PrefixOperator operator
    ) {
        super(thisObj, type, right.getNumberOfChildren() + 1, fileName, lineNumber, pos);
        this.right = right;
        this.operator = operator;
    }

    public Expression getRight() {
        return right;
    }

    public Operator getOperator() {
        return operator;
    }

    static PrefixExpression from(Expression right, PrefixOperator operator) throws OperationNotSupportedException {
        Type returnType = operator.returnsTo(right.getType());
        return new PrefixExpression(
            right.getThisObjType(),
            returnType,
            right.getFileName(),
            right.getLineNumber(),
            right.getPos(),
            right,
            operator
        );
    }

    @Override
    public String toString() {
        return "(" + this.operator + "(" + this.right + "): " + this.getType().getName() + ")";
    }
}
