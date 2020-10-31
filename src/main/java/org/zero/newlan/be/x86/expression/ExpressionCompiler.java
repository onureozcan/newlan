package org.zero.newlan.be.x86.expression;

import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.operator.AdditionOperator;
import org.zero.newlan.fe.operator.SubtractionOperator;
import org.zero.newlan.fe.type.IntegralType;

public class ExpressionCompiler {

    public static String compile(Expression expression) {
        if (expression instanceof AtomicExpression) {
            AtomicExpression atom = (AtomicExpression) expression;
            return compileAtom(atom);
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            return compileBinaryExpression(binaryExpression);
        }
        return null;
    }

    private static String compileBinaryExpression(BinaryExpression binaryExpression) {
        if (binaryExpression.getOperator() instanceof AdditionOperator
            && binaryExpression.getType() instanceof IntegralType) {
            return intToIntAddition(binaryExpression);
        } else if (binaryExpression.getOperator() instanceof SubtractionOperator
            && binaryExpression.getType() instanceof IntegralType) {
            return intToIntSubtraction(binaryExpression);
        }
        return null;
    }

    private static String intToIntSubtraction(BinaryExpression binaryExpression) {
        return compile(binaryExpression.getLeft())
            + compile(binaryExpression.getRight())
            + "pop ebx\n"
            + "pop eax\n"
            + "sub eax, ebx\n"
            + "push eax\n";
    }

    private static String intToIntAddition(BinaryExpression binaryExpression) {
        return compile(binaryExpression.getLeft())
            + compile(binaryExpression.getRight())
            + "pop ebx\n"
            + "pop eax\n"
            + "add eax, ebx\n"
            + "push eax\n";
    }

    private static String compileAtom(AtomicExpression atom) {
        if (atom.getType() instanceof IntegralType) {
            return "push " + atom.getData() + "\n";
        }
        return null;
    }

}
