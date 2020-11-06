package org.zero.newlan.fe.type;

public class PropertyNotFoundException extends Exception {

    public PropertyNotFoundException(String propertyName) {
        super("property " + propertyName + " not found");
    }
}
