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

    @Override
    public String toString() {
        return this.getToken();
    }
}
