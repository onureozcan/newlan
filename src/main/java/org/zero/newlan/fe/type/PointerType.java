package org.zero.newlan.fe.type;

public class PointerType extends Type{

    private final Type referringType;

    public PointerType(Type referringType) {
        super("pointer to " + referringType.getName());
        this.referringType = referringType;
    }

    public Type getReferringType() {
        return referringType;
    }

}
