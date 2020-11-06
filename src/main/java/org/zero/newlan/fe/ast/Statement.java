package org.zero.newlan.fe.ast;

import generated.newlanParser.ExpressionContext;
import generated.newlanParser.StatementContext;
import generated.newlanParser.VariableDeclarationContext;
import org.zero.newlan.CompileException;
import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.operator.OperationNotSupportedException;
import org.zero.newlan.fe.operator.OperatorRepo;
import org.zero.newlan.fe.type.ObjectType;
import org.zero.newlan.fe.type.Type;
import org.zero.newlan.fe.type.TypeNotFoundException;
import org.zero.newlan.fe.type.TypeRepo;

public class Statement extends AstNode {

    private AstNode data;

    private Statement(ObjectType thisObj, AstNode data, String fileName, int lineNumber, int pos) {
        super(thisObj, fileName, lineNumber, pos);
        this.data = data;
    }

    public static Statement from(StatementContext statementContext, ObjectType thisObj, String fileName) {
        AstNode node = null;
        ExpressionContext expressionContext = statementContext.expression();
        VariableDeclarationContext variableDeclarationContext = statementContext.variableDeclaration();
        if (expressionContext != null) {
            node = Expression.from(expressionContext, thisObj, fileName);
        } else if (variableDeclarationContext != null) {
            String name = variableDeclarationContext.variableName.getText();
            expressionContext = variableDeclarationContext.expression();
            Expression expression = null;
            String type = (variableDeclarationContext.type != null) ? variableDeclarationContext.type.getText() : null;
            try {
                Type expectedType = type != null ? TypeRepo.getType(type) : null;
                if (expressionContext != null) {
                    expression = Expression.from(expressionContext, thisObj, fileName);
                    Type actualType = expression.getType();
                    if (expectedType != null) {
                        if (!actualType.equals(expectedType)) {
                            throw new CompileException(variableDeclarationContext, fileName,
                                "incompatible types : " + expectedType + " and " + expression.getType()
                            ); // TODO : this should check for assignability
                        }
                    } else {
                        type = actualType.getName();
                    }

                }
                thisObj.addProperty(name, TypeRepo.getType(type));
                if (expression != null) {
                    node = BinaryExpression.from(
                        AtomicExpression.ident(statementContext, name, thisObj, fileName),
                        expression, OperatorRepo.getBinary("=")
                    );
                }
            } catch (TypeNotFoundException | OperationNotSupportedException e) {
                throw new CompileException(variableDeclarationContext, fileName, e);
            }
        }
        return new Statement(thisObj, node, fileName, statementContext.start.getLine(), statementContext.start.getCharPositionInLine());
    }

    public AstNode getData() {
        return data;
    }
}


