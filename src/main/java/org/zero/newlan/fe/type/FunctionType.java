package org.zero.newlan.fe.type;

import java.util.List;

public class FunctionType extends Type {

    private final List<Type> parameterTypes;
    private final Type returnType;

    public FunctionType(List<Type> parameterTypes, Type returnType) {
        super("function");
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public Type getReturnType() {
        return returnType;
    }
}
