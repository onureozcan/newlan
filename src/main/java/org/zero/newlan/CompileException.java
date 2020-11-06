package org.zero.newlan;

import org.antlr.v4.runtime.ParserRuleContext;

public class CompileException extends RuntimeException {

    private CompileException(ParserRuleContext node, String fileName, String reason, Throwable cause) {
        super(reason + " : " + fileName + " at line :" + node.getStart().getLine() + ", pos :" + node.getStart().getCharPositionInLine(), cause);
    }

    public CompileException(ParserRuleContext node, String fileName, String reason) {
        this(node, fileName, reason, null);
    }

    public CompileException(ParserRuleContext node, String fileName, Throwable cause) {
        this(node, fileName, "", cause);
    }

    public CompileException(ParserRuleContext node, String fileName) {
        this(node, fileName, "", null);
    }
}
