package org.zero.newlan.be.x86.expression;

import org.zero.newlan.be.x86.Registers;
import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.ast.expression.PrefixExpression;
import org.zero.newlan.fe.operator.AdditionOperator;
import org.zero.newlan.fe.operator.DivisionOperator;
import org.zero.newlan.fe.operator.MultiplicationOperator;
import org.zero.newlan.fe.operator.NegativeOperator;
import org.zero.newlan.fe.operator.Operator;
import org.zero.newlan.fe.operator.SubtractionOperator;
import org.zero.newlan.fe.type.FloatingPointType;
import org.zero.newlan.fe.type.IntegralType;

public class ExpressionCompiler {

    private Program program;
    private Registers r;

    public ExpressionCompiler(Program program, Registers registers) {
        this.program = program;
        this.r = registers;
    }

    public void compile(Expression expression) {
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

    private void compilePrefixExpression(PrefixExpression prefixExpression) {
        if (prefixExpression.getType() instanceof IntegralType) {
            Operator operator = prefixExpression.getOperator();
            if (operator instanceof NegativeOperator) {
                numericNegInt(prefixExpression);
            }
        }
    }

    private void numericNegInt(PrefixExpression prefixExpression) {
        compile(prefixExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.NEG).op(r.AX);
        program.addInstruction(Opcode.PUSH).op(r.AX);
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
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.SUB).op(r.AX).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    private void intToIntAddition(BinaryExpression binaryExpression) {
        compile(binaryExpression.getLeft());
        compile(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.ADD).op(r.AX).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    private void intToIntDivision(BinaryExpression binaryExpression) {
        compile(binaryExpression.getLeft());
        compile(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.CDQ);
        program.addInstruction(Opcode.IDIV).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    private void intToIntMultiplication(BinaryExpression binaryExpression) {
        compile(binaryExpression.getLeft());
        compile(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.IMUL).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    private void compileAtom(AtomicExpression atom) {
        if (atom.getType() instanceof IntegralType) {
            program.addInstruction(Opcode.PUSH).op(atom.getData()).comment(atom.toString());
        }
    }

}
