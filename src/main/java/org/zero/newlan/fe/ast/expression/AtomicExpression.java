package org.zero.newlan.fe.ast.expression;

import generated.newlanParser.AtomContext;
import org.zero.newlan.fe.type.AnyType;
import org.zero.newlan.fe.type.FloatingPointType;
import org.zero.newlan.fe.type.IntegralType;
import org.zero.newlan.fe.type.PointerType;
import org.zero.newlan.fe.type.StringType;
import org.zero.newlan.fe.type.Type;

public class AtomicExpression extends Expression {

    private final String data;

    private AtomicExpression(Type type, String data, String fileName, int lineNumber, int pos) {
        super(type, fileName, lineNumber, pos);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    static AtomicExpression from(AtomContext atomContext, String fileName) {
        Type type = null;
        String data = atomContext.getText();
        if (atomContext.DECIMAL() != null) {
            type = new FloatingPointType();
        } else if (atomContext.STRING() != null) {
            type = new StringType();
        } else if (atomContext.INT() != null) {
            type = new IntegralType();
        } else if (atomContext.IDENT() != null) {
            type = new PointerType(
                new AnyType() // TODO: resolve its type from context object
            );
        }
        return new AtomicExpression(
            type,
            data,
            fileName,
            atomContext.getStart().getLine(),
            atomContext.getStart().getCharPositionInLine()
        );
    }

    @Override
    public String toString() {
        return "(" + this.getData() + ":" + this.getType().getName() + ")";
    }
}
