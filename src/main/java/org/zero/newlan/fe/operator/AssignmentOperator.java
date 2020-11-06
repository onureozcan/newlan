package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.Type;

public class AssignmentOperator extends BinaryOperator {

    AssignmentOperator() {
        super("=");
    }

    @Override
    public Type returnsTo(Type t1, Type t2) throws OperationNotSupportedException {
        if (t1.equals(t2)) { // TODO: replace this with isAssignableFrom
            return t1;
        } else {
            throw new OperationNotSupportedException(this, t1, t2);
        }
    }
}
