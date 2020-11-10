package org.zero.newlan.fe.ast.expression;

import generated.newlanParser.AtomContext;
import generated.newlanParser.ExpressionContext;
import generated.newlanParser.PrimaryExpresssionContext;
import org.zero.newlan.CompileException;
import org.zero.newlan.fe.ast.AstNode;
import org.zero.newlan.fe.operator.BinaryOperator;
import org.zero.newlan.fe.operator.OperationNotSupportedException;
import org.zero.newlan.fe.operator.OperatorRepo;
import org.zero.newlan.fe.operator.PrefixOperator;
import org.zero.newlan.fe.type.ObjectType;
import org.zero.newlan.fe.type.Type;

public class Expression extends AstNode {

    private Type type;

    private int numberOfChildren;

    Expression(ObjectType contextObjectType, Type type, int numberOfChildren, String fileName, int lineNumber, int pos) {
        super(contextObjectType, fileName, lineNumber, pos);
        this.type = type;
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public Type getType() {
        return type;
    }

    public static Expression from(ExpressionContext expressionContext, ObjectType contextObject, String fileName) {
        PrimaryExpresssionContext primaryExpresssionContext = expressionContext.primaryExpresssion();
        if (primaryExpresssionContext != null) {
            AtomContext atom = primaryExpresssionContext.atom();
            ExpressionContext expression = primaryExpresssionContext.expression();
            if (expression != null) { return from(expression, contextObject, fileName); }
            if (atom != null) { return AtomicExpression.from(atom, contextObject, fileName); }
        } else if (expressionContext.bop != null) {
            String bopToken = expressionContext.bop.getText();
            BinaryOperator op = OperatorRepo.getBinary(bopToken);
            if (op == null) {
                throw new CompileException(expressionContext, fileName, "No operation found : " + bopToken);
            }
            try {
                return BinaryExpression.from(
                    from(expressionContext.expression(0), contextObject, fileName),
                    from(expressionContext.expression(1), contextObject, fileName),
                    op
                );
            } catch (OperationNotSupportedException e) {
                throw new CompileException(expressionContext, fileName, e);
            }
        } else if (expressionContext.prefix != null) {
            String prefixToken = expressionContext.prefix.getText();
            PrefixOperator op = OperatorRepo.getPrefix(prefixToken);
            try {
                return PrefixExpression.from(
                    from(expressionContext.expression(0), contextObject, fileName),
                    op
                );
            } catch (OperationNotSupportedException e) {
                throw new CompileException(expressionContext, fileName, e);
            }
        } else if (expressionContext.methodCall != null) {
            return FunctionCallExpression.from(expressionContext, contextObject, fileName);
        }
        throw new CompileException(expressionContext, fileName);
    }
}
