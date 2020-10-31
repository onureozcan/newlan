package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.StringType;
import org.zero.newlan.fe.type.Type;

class AdditionOperator extends NumericOperator {

    AdditionOperator() {
        super("+");
    }

    @Override
    public Type returnsTo(Type t1, Type t2) throws OperationNotSupportedException {
        if (t1 instanceof StringType || t2 instanceof StringType) return new StringType();
        return super.returnsTo(t1, t2);
    }
}
