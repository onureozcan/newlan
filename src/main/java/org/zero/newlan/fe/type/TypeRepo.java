package org.zero.newlan.fe.type;

import java.util.HashMap;
import java.util.Map;

public class TypeRepo {

    private final static Map<String, Type> KNOWN_TYPES = new HashMap<>();

    static {
        addType(new FloatingPointType());
        addType(new IntegralType());
        addType(new StringType());
        addType(new AnyType());
        addType(new ObjectType("$Global"));
    }

    static void addType(Type type) {
        KNOWN_TYPES.put(type.getName(), type);
    }

    public static <T extends Type> T getType(String name) throws TypeNotFoundException {
        if (KNOWN_TYPES.containsKey(name)) {
            return (T) KNOWN_TYPES.get(name);
        } else {
            throw new TypeNotFoundException(name);
        }
    }
}
