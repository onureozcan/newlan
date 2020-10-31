package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.FloatingPointType;
import org.zero.newlan.fe.type.IntegralType;
import org.zero.newlan.fe.type.PointerType;
import org.zero.newlan.fe.type.Type;

class NumericOperator extends Operator{

    NumericOperator(String token) {
        super(token);
    }

    @Override
    public Type returnsTo(Type t1, Type t2) throws OperationNotSupportedException {
        if (t1 instanceof PointerType) {
            t1 = ((PointerType) t1).getReferringType();
        }
        if (t2 instanceof PointerType) {
            t2 = ((PointerType) t2).getReferringType();
        }
        if (t1 instanceof IntegralType) {
            if (t2 instanceof FloatingPointType) {
                return new FloatingPointType();
            } else if (t2 instanceof IntegralType) {
                return new IntegralType();
            } else {
                throw new OperationNotSupportedException(this, t1, t2);
            }
        } else if (t1 instanceof FloatingPointType) {
            if (t2 instanceof FloatingPointType) {
                return new FloatingPointType();
            } else if (t2 instanceof IntegralType) {
                return new IntegralType();
            } else {
                throw new OperationNotSupportedException(this, t1, t2);
            }
        } else {
            throw new OperationNotSupportedException(this, t1, t2);
        }
    }
}
