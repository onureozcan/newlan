package org.zero.newlan.fe.ast;

import org.zero.newlan.fe.type.ObjectType;

public class AstNode {

    private final String fileName;
    private final int lineNumber;
    private final int pos;
    private ObjectType contextObjectType;

    public AstNode(ObjectType contextObjectType, String fileName, int lineNumber, int pos) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.pos = pos;
        this.contextObjectType = contextObjectType;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getPos() {
        return pos;
    }

    public ObjectType getContextObjectType() {
        return contextObjectType;
    }
}
