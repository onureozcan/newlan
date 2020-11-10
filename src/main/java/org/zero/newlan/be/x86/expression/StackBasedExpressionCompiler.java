package org.zero.newlan.be.x86.expression;

import org.zero.newlan.be.x86.Registers;
import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.ast.expression.FunctionCallExpression;
import org.zero.newlan.fe.ast.expression.PrefixExpression;
import org.zero.newlan.fe.type.IntegralType;

public class StackBasedExpressionCompiler extends ExpressionCompiler {

    public StackBasedExpressionCompiler(Program program, Registers registers) {
        super(program, registers);
    }

    @Override
    public void compileExpression(Expression expression) {
        super.compileInternal(expression);
        program.addInstruction(Opcode.POP).op(r.AX);
    }

    @Override
    void compileFunctionCall(FunctionCallExpression functionCallExpression) {

    }

    @Override
    void compileAssignment(BinaryExpression binaryExpression) {

    }

    void numericNegInt(PrefixExpression prefixExpression) {
        compileInternal(prefixExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.NEG).op(r.AX);
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    void intToIntSubtraction(BinaryExpression binaryExpression) {
        compileInternal(binaryExpression.getLeft());
        compileInternal(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.SUB).op(r.AX).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    void intToIntAddition(BinaryExpression binaryExpression) {
        compileInternal(binaryExpression.getLeft());
        compileInternal(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.ADD).op(r.AX).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    void intToIntDivision(BinaryExpression binaryExpression) {
        compileInternal(binaryExpression.getLeft());
        compileInternal(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.CDQ);
        program.addInstruction(Opcode.IDIV).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    void intToIntMultiplication(BinaryExpression binaryExpression) {
        compileInternal(binaryExpression.getLeft());
        compileInternal(binaryExpression.getRight());
        program.addInstruction(Opcode.POP).op(r.BX);
        program.addInstruction(Opcode.POP).op(r.AX);
        program.addInstruction(Opcode.IMUL).op(r.BX).comment(binaryExpression.toString());
        program.addInstruction(Opcode.PUSH).op(r.AX);
    }

    void compileAtom(AtomicExpression atom) {
        if (atom.getType() instanceof IntegralType) {
            program.addInstruction(Opcode.PUSH).op(atom.getData()).comment(atom.toString());
        }
    }
}
