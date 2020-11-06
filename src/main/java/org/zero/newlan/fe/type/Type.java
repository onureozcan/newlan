package org.zero.newlan.fe.type;

public abstract class Type {

    private String name;

    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Type && name.equals(((Type) obj).name);
    }
}
