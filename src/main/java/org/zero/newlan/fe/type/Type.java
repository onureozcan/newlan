package org.zero.newlan.fe.type;

public abstract class Type {

    private String name;

    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
