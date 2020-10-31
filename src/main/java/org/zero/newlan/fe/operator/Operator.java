package org.zero.newlan.fe.operator;

import org.zero.newlan.fe.type.Type;

public abstract class Operator {

    private final String token;

    Operator(String token) {
        this.token = token;
    }

    String getToken() {
        return token;
    }

    public abstract Type returnsTo(Type t1, Type t2) throws OperationNotSupportedException;

    @Override
    public String toString() {
        return this.getToken();
    }
}
