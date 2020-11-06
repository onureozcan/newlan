package org.zero.newlan.be.x86.expression;

import org.zero.newlan.be.x86.Registers;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.ast.expression.PrefixExpression;
import org.zero.newlan.fe.operator.AdditionOperator;
import org.zero.newlan.fe.operator.AssignmentOperator;
import org.zero.newlan.fe.operator.DivisionOperator;
import org.zero.newlan.fe.operator.MultiplicationOperator;
import org.zero.newlan.fe.operator.NegativeOperator;
import org.zero.newlan.fe.operator.Operator;
import org.zero.newlan.fe.operator.SubtractionOperator;
import org.zero.newlan.fe.type.FloatingPointType;
import org.zero.newlan.fe.type.IntegralType;

public abstract class ExpressionCompiler {

    Program program;
    Registers r;

    ExpressionCompiler(Program program, Registers registers) {
        this.program = program;
        this.r = registers;
    }

    abstract void intToIntAddition(BinaryExpression binaryExpression);
    abstract void intToIntDivision(BinaryExpression binaryExpression);
    abstract void intToIntMultiplication(BinaryExpression binaryExpression);
    abstract void numericNegInt(PrefixExpression prefixExpression);
    abstract void intToIntSubtraction(BinaryExpression binaryExpression);
    abstract void compileAtom(AtomicExpression atom);

    public void compileExpression(Expression expression) {
        compileInternal(expression);
    }

    void compileInternal(Expression expression) {
        if (expression instanceof AtomicExpression) {
            AtomicExpression atom = (AtomicExpression) expression;
            compileAtom(atom);
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            compileBinaryExpression(binaryExpression);
        } else if (expression instanceof PrefixExpression) {
            PrefixExpression prefixExpression = (PrefixExpression) expression;
            compilePrefixExpression(prefixExpression);
        }
    }

    private void compileBinaryExpression(BinaryExpression binaryExpression) {
        if (binaryExpression.getOperator() instanceof AssignmentOperator) {
            compileAssignment(binaryExpression);
        } else if (binaryExpression.getType() instanceof IntegralType) {
            // int to int operations
            Operator operator = binaryExpression.getOperator();
            if (operator instanceof AdditionOperator) {
                intToIntAddition(binaryExpression);
            } else if (operator instanceof SubtractionOperator) {
                intToIntSubtraction(binaryExpression);
            } else if (operator instanceof MultiplicationOperator) {
                intToIntMultiplication(binaryExpression);
            } else if (operator instanceof DivisionOperator) {
                intToIntDivision(binaryExpression);
            }
        } else if (binaryExpression.getType() instanceof FloatingPointType) {
            // float to float or float to int operations
            // TODO
        }
    }

    abstract void compileAssignment(BinaryExpression binaryExpression);

    private void compilePrefixExpression(PrefixExpression prefixExpression) {
        if (prefixExpression.getType() instanceof IntegralType) {
            Operator operator = prefixExpression.getOperator();
            if (operator instanceof NegativeOperator) {
                numericNegInt(prefixExpression);
            }
        }
    }
}
