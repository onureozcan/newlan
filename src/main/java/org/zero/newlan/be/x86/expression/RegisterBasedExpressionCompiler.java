package org.zero.newlan.be.x86.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.zero.newlan.be.x86.Registers;
import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.ast.expression.PrefixExpression;
import org.zero.newlan.fe.type.IntegralType;
import org.zero.newlan.fe.type.PropertyNotFoundException;

/**
 * RegisterBasedExpressionCompiler Assumes that all the registers are safe to use! return value at AX
 */
public class RegisterBasedExpressionCompiler extends ExpressionCompiler {

    private String valueReg;

    private Map<String, Boolean> registerAvailabilityMap = new HashMap<>(4);

    public RegisterBasedExpressionCompiler(Program program, Registers registers) {
        super(program, registers);
    }

    @Override
    public void compileExpression(Expression expression) {
        registerAvailabilityMap.put(r.AX, true);
        registerAvailabilityMap.put(r.BX, true);
        registerAvailabilityMap.put(r.CX, true);
        //registerAvailabilityMap.put(r.DX, true);
        super.compileExpression(expression);
        program.addInstruction(Opcode.MOV).op(r.AX).op(valueReg);
    }

    @Override
    void compileAssignment(BinaryExpression binaryExpression) {
        compileInternal(binaryExpression.getRight());
        String r2 = valueReg;
        compileInternal(binaryExpression.getLeft());
        String r1 = valueReg;
        program.addInstruction(Opcode.MOV).op("[" + r1 + "]").op(r2).comment(binaryExpression.toString());
        freeRegister(r1);
        valueReg = r2;
    }

    void numericNegInt(PrefixExpression prefixExpression) {
        compileInternal(prefixExpression.getRight());
        program.addInstruction(Opcode.NEG).op(valueReg);
    }

    void intToIntSubtraction(BinaryExpression binaryExpression) {
        splitBinaryExp(binaryExpression, (r1, r2) -> {
            program.addInstruction(Opcode.SUB).op(r1).op(r2).comment(binaryExpression.toString());
        });
    }

    void intToIntAddition(BinaryExpression binaryExpression) {
        splitBinaryExp(binaryExpression, (r1, r2) -> {
            program.addInstruction(Opcode.ADD).op(r1).op(r2).comment(binaryExpression.toString());
        });
    }

    void intToIntDivision(BinaryExpression binaryExpression) {
        splitBinaryExp(binaryExpression, (r1, r2) -> {
            String dividerReg = r2;
            String dividedReg = r1;
            boolean saveAx = !r1.equals(r.AX) && !r2.equals(r.AX);
            if (r2.equals(r.AX)) {
                program.addInstruction(Opcode.XCHG).op(r1).op(r2);
                dividerReg = r1;
                dividedReg = r2;
            }
            if (saveAx) {
                program.addInstruction(Opcode.PUSH).op(r.AX);
                program.addInstruction(Opcode.MOV).op(r.AX).op(dividedReg);
            }
            program.addInstruction(Opcode.CDQ);
            program.addInstruction(Opcode.IDIV).op(dividerReg).comment(binaryExpression.toString());
            if (!r1.equals(r.AX)) {
                program.addInstruction(Opcode.MOV).op(r1).op(r.AX);
            }
            if (saveAx) {
                program.addInstruction(Opcode.POP).op(r.AX);
            }
        });
    }

    void intToIntMultiplication(BinaryExpression binaryExpression) {
        splitBinaryExp(binaryExpression, (r1, r2) -> {
            String multiplierReg = r1;
            String multiplicatedReg = r2;
            boolean saveAx = !r1.equals(r.AX) && !r2.equals(r.AX);
            if (saveAx) {
                program.addInstruction(Opcode.PUSH).op(r.AX);
            }
            if (r1.equals(r.AX)) {
                multiplierReg = r2;
                multiplicatedReg = r1;
            }
            if (r2.equals(r.AX)) {
                multiplierReg = r1;
                multiplicatedReg = r2;
            }
            if (!multiplicatedReg.equals(r.AX)) {
                program.addInstruction(Opcode.MOV).op(r.AX).op(multiplicatedReg);
            }
            program.addInstruction(Opcode.IMUL).op(multiplierReg).comment(binaryExpression.toString());
            if (!r1.equals(r.AX)) {
                program.addInstruction(Opcode.MOV).op(r1).op(r.AX);
            }
            if (saveAx) {
                program.addInstruction(Opcode.POP).op(r.AX);
            }
        });
    }

    void compileAtom(AtomicExpression atom) {
        if (atom.getType() instanceof IntegralType) {
            valueReg = getRegister();
            if (atom.isIdent()) {
                try {
                    int index = atom.getThisObjType().getIndexOf(atom.getData());
                    // Base pointer points at this
                    program.addInstruction(Opcode.MOV).op(valueReg).op("[" + r.BP + " + " + (index * r.sizeOfInt()) + "]")
                        .comment(atom.toString());
                } catch (PropertyNotFoundException e) {
                    throw new RuntimeException("this should not have happened!");
                }
            } else {
                program.addInstruction(Opcode.MOV).op(valueReg).op(atom.getData()).comment(atom.toString());
            }
        }
    }

    private void splitBinaryExp(BinaryExpression binaryExpression, BiConsumer<String, String> consumer) {
        Expression deep, shallow;
        if (binaryExpression.getLeft().getNumberOfChildren() > binaryExpression.getRight().getNumberOfChildren()) {
            deep = binaryExpression.getLeft();
            shallow = binaryExpression.getRight();
        } else {
            deep = binaryExpression.getRight();
            shallow = binaryExpression.getLeft();
        }
        compileInternal(deep);
        String deepReg = valueReg;
        compileInternal(shallow);
        String shallowReg = valueReg;
        if (deep == binaryExpression.getLeft()) {
            valueReg = deepReg;
            freeRegister(shallowReg);
            consumer.accept(valueReg, shallowReg);
        } else {
            valueReg = shallowReg;
            freeRegister(deepReg);
            consumer.accept(valueReg, deepReg);
        }
    }

    private String getRegister() {
        Optional<String> freeRegOptional = registerAvailabilityMap.entrySet().stream().filter(Entry::getValue).map(Entry::getKey)
            .findFirst();
        freeRegOptional.ifPresent(s -> registerAvailabilityMap.put(s, false));
        return freeRegOptional.get(); // in a sane world, we can never run out of registers with binary ops
    }

    private void freeRegister(String reg) {
        registerAvailabilityMap.put(reg, true);
    }
}
