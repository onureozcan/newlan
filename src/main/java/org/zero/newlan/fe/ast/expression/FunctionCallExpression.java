package org.zero.newlan.fe.ast.expression;

import generated.newlanParser.ExpressionContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.zero.newlan.CompileException;
import org.zero.newlan.fe.type.FunctionType;
import org.zero.newlan.fe.type.ObjectType;
import org.zero.newlan.fe.type.Type;

public class FunctionCallExpression extends Expression {

    private Expression callee;

    private List<Expression> arguments;

    private FunctionCallExpression(
        Expression callee,
        List<Expression> arguments,
        ObjectType contextObjectType,
        Type type,
        int numberOfChildren,
        String fileName,
        int lineNumber,
        int pos
    ) {
        super(contextObjectType, type, numberOfChildren, fileName, lineNumber, pos);
        this.callee = callee;
        this.arguments = arguments;
    }

    public static FunctionCallExpression from(ExpressionContext expressionContext, ObjectType contextObject, String fileName) {
        Expression functionToCall = Expression.from(expressionContext.expression(0), contextObject, fileName);
        Type type = functionToCall.getType();
        if (!(type instanceof FunctionType)) {
            throw new CompileException(expressionContext.expression(0), fileName, "not a function");
        }
        FunctionType functionType = (FunctionType) type;

        List<Expression> arguments = expressionContext.expression().stream().skip(1).map(
            e -> Expression.from(e, contextObject, fileName)
        ).collect(Collectors.toList());

        IntStream.range(0, arguments.size()).forEach(i -> {
            if (!arguments.get(i).getType().equals(functionType.getParameterTypes().get(i))) {
                throw new CompileException(expressionContext.expression(0), fileName, "arg " + i + " is not applicable");
            }
        });

        return new FunctionCallExpression(
            functionToCall,
            arguments,
            contextObject,
            functionType.getReturnType(),
            arguments.size() + 1,
            fileName,
            expressionContext.start.getLine(),
            expressionContext.start.getCharPositionInLine()
        );
    }

    public Expression getCallee() {
        return callee;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return callee + "(" + arguments.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }
}
