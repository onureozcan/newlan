package org.zero.newlan.be.x86.expression;

import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.operator.AdditionOperator;
import org.zero.newlan.fe.operator.DivisionOperator;
import org.zero.newlan.fe.operator.MultiplicationOperator;
import org.zero.newlan.fe.operator.Operator;
import org.zero.newlan.fe.operator.SubtractionOperator;
import org.zero.newlan.fe.type.FloatingPointType;
import org.zero.newlan.fe.type.IntegralType;

public class ExpressionCompiler {

    private Program program;

    public ExpressionCompiler(Program program) {
        this.program = program;
    }

    public void compile(Expression expression) {
        if (expression instanceof AtomicExpression) {
            AtomicExpression atom = (AtomicExpression) expression;
            compileAtom(atom);
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            compileBinaryExpression(binaryExpression);
        }
    }

    private void compileBinaryExpression(BinaryExpression binaryExpression) {
        if (binaryExpression.getType() instanceof IntegralType) {
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

    private void intToIntSubtraction(BinaryExpression binaryExpression) {
        compile(binaryExpression.getLeft());
        compile(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op("ebx");
        program.addInstruction(Opcode.POP).op("eax");
        program.addInstruction(Opcode.SUB).op("ebx").op("eax").comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op("eax");
    }

    private void intToIntAddition(BinaryExpression binaryExpression) {
        compile(binaryExpression.getLeft());
        compile(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op("ebx");
        program.addInstruction(Opcode.POP).op("eax");
        program.addInstruction(Opcode.ADD).op("ebx").op("eax").comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op("eax");
    }

    private void intToIntDivision(BinaryExpression binaryExpression) {
        compile(binaryExpression.getLeft());
        compile(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op("ebx");
        program.addInstruction(Opcode.POP).op("eax");
        program.addInstruction(Opcode.IDIV).op("ebx").comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op("eax");
    }

    private void intToIntMultiplication(BinaryExpression binaryExpression) {
        compile(binaryExpression.getLeft());
        compile(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op("ebx");
        program.addInstruction(Opcode.POP).op("eax");
        program.addInstruction(Opcode.IMUL).op("ebx").comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op("eax");
    }

    private void compileAtom(AtomicExpression atom) {
        if (atom.getType() instanceof IntegralType) {
            program.addInstruction(Opcode.PUSH).op(atom.getData()).comment(atom.toString());
        }
    }

}
