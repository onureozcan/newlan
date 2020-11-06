package org.zero.newlan.fe.type;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectType extends Type {

    private final Map<String, Property> properties = new LinkedHashMap<>();

    ObjectType(String name) {
        super(name);
    }

    public void addProperty(String name, Type type) {
        properties.put(name, new Property(name, type,false, false, properties.size()));
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public int getIndexOf(String property) throws PropertyNotFoundException {
        if (properties.containsKey(property)) {
            return properties.get(property).index;
        } else {
            throw new PropertyNotFoundException(property);
        }
    }

    public Type getTypeOf(String property) throws PropertyNotFoundException {
        if (properties.containsKey(property)) {
            return properties.get(property).type;
        } else {
            throw new PropertyNotFoundException(property);
        }
    }

    private class Property {

        private String name;

        private boolean isPrivate; // TODO

        private boolean isFinal; // TODO

        private Type type;

        private int index;

        private Property(String name, Type type, boolean isPrivate, boolean isFinal, int index) {
            this.name = name;
            this.isPrivate = isPrivate;
            this.isFinal = isFinal;
            this.type = type;
            this.index = index;
        }
    }
}
