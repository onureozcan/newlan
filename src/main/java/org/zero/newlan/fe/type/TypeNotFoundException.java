package org.zero.newlan.fe.type;

public class TypeNotFoundException extends Exception {

    public TypeNotFoundException(String type) {
        super("type " + type + " not found");
    }
}
