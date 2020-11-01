package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.Type;

public abstract class BinaryOperator extends Operator {

    BinaryOperator(String token) {
        super(token);
    }

    public abstract Type returnsTo(Type t1, Type t2) throws OperationNotSupportedException;
}
