package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.Type;

public class OperationNotSupportedException extends Exception {

    OperationNotSupportedException(Operator operator, Type t1, Type t2) {
        super("operator " + operator.getToken() + " does not operate on " + t1.getName() + " and " + t2.getName());
    }

    OperationNotSupportedException(Operator operator, Type t1) {
        super("operator " + operator.getToken() + " does not operate on " + t1.getName());
    }
}
