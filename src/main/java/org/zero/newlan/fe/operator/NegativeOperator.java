package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.FloatingPointType;
import org.zero.newlan.fe.type.IntegralType;
import org.zero.newlan.fe.type.Type;

public class NegativeOperator extends PrefixOperator {

    NegativeOperator() {
        super("-");
    }

    @Override
    public Type returnsTo(Type type) throws OperationNotSupportedException {
        if (type instanceof IntegralType || type instanceof FloatingPointType) {
            return type;
        } else {
            throw new OperationNotSupportedException(this, type);
        }
    }
}
