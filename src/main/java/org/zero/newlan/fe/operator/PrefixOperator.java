package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.Type;

public abstract class PrefixOperator extends Operator{

    PrefixOperator(String token) {
        super(token);
    }

    public abstract Type returnsTo(Type type) throws OperationNotSupportedException;
}
