package org.zero.newlan.fe.ast.expression;

import generated.newlanParser.AtomContext;
import generated.newlanParser.ExpressionContext;
import generated.newlanParser.PrimaryExpresssionContext;
import org.zero.newlan.fe.FeException;
import org.zero.newlan.fe.ast.AstNode;
import org.zero.newlan.fe.operator.BinaryOperator;
import org.zero.newlan.fe.operator.OperationNotSupportedException;
import org.zero.newlan.fe.operator.OperatorRepo;
import org.zero.newlan.fe.operator.PrefixOperator;
import org.zero.newlan.fe.type.Type;

public class Expression extends AstNode {

    private Type type;
    private int numberOfChildren;

    Expression(Type type, int numberOfChildren, String fileName, int lineNumber, int pos) {
        super(fileName, lineNumber, pos);
        this.type = type;
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
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
        } else if (expressionContext.bop != null) {
            String bopToken = expressionContext.bop.getText();
            BinaryOperator op = OperatorRepo.getBinary(bopToken);
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
        } else if (expressionContext.prefix != null) {
            String prefixToken = expressionContext.prefix.getText();
            PrefixOperator op = OperatorRepo.getPrefix(prefixToken);
            try {
                return PrefixExpression.from(
                    from(expressionContext.expression(0), fileName),
                    op
                );
            } catch (OperationNotSupportedException e) {
                throw new FeException(expressionContext, fileName, e);
            }
        }
        throw new FeException(expressionContext, fileName);
    }
}
