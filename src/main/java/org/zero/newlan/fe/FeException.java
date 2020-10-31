package org.zero.newlan.fe;

import org.antlr.v4.runtime.ParserRuleContext;

public class FeException extends RuntimeException {


    public FeException(ParserRuleContext node, String fileName, String reason, Throwable cause) {
        super(reason + " : " + fileName + " at line :" + node.getStart().getLine() + ", pos :" + node.getStart().getCharPositionInLine(), cause);
    }

    public FeException(ParserRuleContext node, String fileName, String reason) {
        this(node, fileName, reason, null);
    }

    public FeException(ParserRuleContext node, String fileName, Throwable cause) {
        this(node, fileName, "", cause);
    }

    public FeException(ParserRuleContext node, String fileName) {
        this(node, fileName, "", null);
    }
}
