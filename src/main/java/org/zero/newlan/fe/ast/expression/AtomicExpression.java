package org.zero.newlan.fe.ast.expression;

import generated.newlanParser.AtomContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.zero.newlan.CompileException;
import org.zero.newlan.fe.type.FloatingPointType;
import org.zero.newlan.fe.type.IntegralType;
import org.zero.newlan.fe.type.ObjectType;
import org.zero.newlan.fe.type.PointerType;
import org.zero.newlan.fe.type.PropertyNotFoundException;
import org.zero.newlan.fe.type.StringType;
import org.zero.newlan.fe.type.Type;

public class AtomicExpression extends Expression {

    private final String data;
    private boolean isIdent;

    private AtomicExpression(ObjectType contextObjectType, Type type, String data, boolean isIdent, String fileName, int lineNumber, int pos) {
        super(contextObjectType, type, 0, fileName, lineNumber, pos);
        this.data = data;
        this.isIdent = isIdent;
    }

    public boolean isIdent() {
        return isIdent;
    }

    public String getData() {
        return data;
    }

    static AtomicExpression from(AtomContext atomContext, ObjectType thisObj, String fileName) {
        Type type = null;
        String data = atomContext.getText();
        boolean isIdent = false;
        if (atomContext.DECIMAL() != null) {
            type = new FloatingPointType();
        } else if (atomContext.STRING() != null) {
            type = new StringType();
        } else if (atomContext.INT() != null) {
            type = new IntegralType();
        } else if (atomContext.IDENT() != null) {
            try {
                isIdent = true;
                type = thisObj.getTypeOf(data);
                if (type instanceof ObjectType) {
                    // not primitive, this is a pointer
                    type = new PointerType(type);
                }
            } catch (PropertyNotFoundException e) {
                throw new CompileException(atomContext, fileName, e);
            }
        }
        return new AtomicExpression(
            thisObj,
            type,
            data,
            isIdent,
            fileName,
            atomContext.getStart().getLine(),
            atomContext.getStart().getCharPositionInLine()
        );
    }

    public static AtomicExpression ident(ParserRuleContext parent, String data, ObjectType thisObj, String fileName) {
        Type type;
        try {
            type = thisObj.getTypeOf(data);
            if (type instanceof ObjectType) {
                // not primitive, this is a pointer
                type = new PointerType(type);
            }
        } catch (PropertyNotFoundException e) {
            throw new CompileException(parent, fileName, e);
        }
        return new AtomicExpression(
            thisObj,
            type,
            data,
            true,
            fileName,
            parent.getStart().getLine(),
            parent.getStart().getCharPositionInLine()
        );
    }

    @Override
    public String toString() {
        return "(" + this.getData() + ":" + this.getType().getName() + ")";
    }
}
