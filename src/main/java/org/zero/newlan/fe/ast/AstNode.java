package org.zero.newlan.fe.ast;

public class AstNode {

    private final String fileName;
    private final int lineNumber;
    private final int pos;

    public AstNode(String fileName, int lineNumber, int pos) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.pos = pos;
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
}
