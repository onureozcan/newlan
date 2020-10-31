package org.zero.newlan.fe.ast.expression;

import generated.newlanParser.AtomContext;
import generated.newlanParser.ExpressionContext;
import generated.newlanParser.PrimaryExpresssionContext;
import org.zero.newlan.fe.FeException;
import org.zero.newlan.fe.ast.AstNode;
import org.zero.newlan.fe.operator.OperationNotSupportedException;
import org.zero.newlan.fe.operator.Operator;
import org.zero.newlan.fe.operator.OperatorRepo;
import org.zero.newlan.fe.type.Type;

public class Expression extends AstNode {

    private Type type;

    Expression(Type type, String fileName, int lineNumber, int pos) {
        super(fileName, lineNumber, pos);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public static Expression from(ExpressionContext expressionContext, String fileName) {
        PrimaryExpresssionContext primaryExpresssionContext = expressionContext.primaryExpresssion();
        if (primaryExpresssionContext != null) {
            AtomContext atom = primaryExpresssionContext.atom();
            ExpressionContext expression = primaryExpresssionContext.expression();
            if (expression != null) return from(expression, fileName);
            if (atom != null) return AtomicExpression.from(atom, fileName);
        } else if (expressionContext.bop != null){
            String bopToken = expressionContext.bop.getText();
            Operator op = OperatorRepo.get(bopToken);
            if (op == null) {
                throw new FeException(expressionContext, fileName,"No operation found : " + bopToken);
            }
            try {
                return BinaryExpression.from(
                    from(expressionContext.expression(0), fileName),
                    from(expressionContext.expression(1), fileName),
                    op
                );
            } catch (OperationNotSupportedException e) {
                throw new FeException(expressionContext, fileName, e);
            }
        }
        throw new FeException(expressionContext, fileName);
    }
}
