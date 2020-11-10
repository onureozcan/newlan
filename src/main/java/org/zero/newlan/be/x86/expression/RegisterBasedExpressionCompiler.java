package org.zero.newlan.be.x86.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.zero.newlan.be.x86.Registers;
import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.AtomicExpression;
import org.zero.newlan.fe.ast.expression.BinaryExpression;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.ast.expression.FunctionCallExpression;
import org.zero.newlan.fe.ast.expression.PrefixExpression;
import org.zero.newlan.fe.type.FunctionType;
import org.zero.newlan.fe.type.IntegralType;
import org.zero.newlan.fe.type.PropertyNotFoundException;

/**
 * RegisterBasedExpressionCompiler Assumes that all the registers are safe to use! return value at AX
 */
public class RegisterBasedExpressionCompiler extends ExpressionCompiler {

    private String valueReg;

    private static final String STACK = "STACK";

    private Map<String, Boolean> registerAvailabilityMap = new HashMap<>(4);

    public RegisterBasedExpressionCompiler(Program program, Registers registers) {
        super(program, registers);
    }

    @Override
    public void compileExpression(Expression expression) {
        registerAvailabilityMap.put(r.AX, true);
        registerAvailabilityMap.put(r.BX, true);
        registerAvailabilityMap.put(r.CX, true);
        super.compileExpression(expression);
        if (!valueReg.equals(STACK)) {
            if (!r.AX.equals(valueReg)) {
                program.addInstruction(Opcode.MOV).op(r.AX).op(valueReg);
            }
        } else {
            program.addInstruction(Opcode.POP).op(r.AX);
        }
    }

    @Override
    void compileFunctionCall(FunctionCallExpression functionCallExpression) {
        compileInternal(functionCallExpression.getCallee());
        String returnReg = valueReg;
        String calleeReg = valueReg;
//        if (!returnReg.equals(r.AX)) { program.addInstruction(Opcode.PUSH).op(r.AX).comment("save ax before call"); }
//        if (!returnReg.equals(r.BX)) { program.addInstruction(Opcode.PUSH).op(r.BX).comment("save bx before call"); }
//        if (!returnReg.equals(r.CX)) { program.addInstruction(Opcode.PUSH).op(r.CX).comment("save cx before call"); }
        if (calleeReg.equals(STACK)) {
            calleeReg = r.AX;
            program.addInstruction(Opcode.POP).op(calleeReg);
        }
        functionCallExpression.getArguments().stream().sorted(Collections.reverseOrder()).forEach(e -> {
            compileInternal(e);
            if (!valueReg.equals(STACK)) {
                program.addInstruction(Opcode.PUSH).op(valueReg);
                freeRegister(valueReg);
            }
        });
        program.addInstruction(Opcode.CALL).op(calleeReg).comment(functionCallExpression.toString());
        if (returnReg.equals(STACK)) {
            program.addInstruction(Opcode.MOV).op(r.DX).op(r.AX).comment("call result temporarily to dx");
        }
//        if (!returnReg.equals(r.CX)) { program.addInstruction(Opcode.POP).op(r.CX).comment("restore cx after call"); }
//        if (!returnReg.equals(r.BX)) { program.addInstruction(Opcode.POP).op(r.BX).comment("restore bx after call"); }
//        if (!returnReg.equals(r.AX)) { program.addInstruction(Opcode.POP).op(r.AX).comment("restore ax after call"); }
        if (returnReg.equals(STACK)) {
            program.addInstruction(Opcode.PUSH).op(r.DX).comment("call result goes to stack");
        } else if (!returnReg.equals(r.AX)) {
            program.addInstruction(Opcode.MOV).op(returnReg).op(r.AX).comment("call result goes to " + returnReg);
        }
        valueReg = returnReg;
    }

    @Override
    void compileAssignment(BinaryExpression binaryExpression) {
        compileInternal(binaryExpression.getRight());
        String r2 = valueReg;
        AtomicExpression atom = (AtomicExpression) binaryExpression.getLeft();
        try {
            int index = atom.getContextObjectType().getIndexOf(atom.getData());
            program.addInstruction(Opcode.MOV).op("[" + r.BP + " - (" + index * r.sizeOfInt() + ")]").op(r2)
                .comment(binaryExpression.toString());
        } catch (PropertyNotFoundException e) {
            // ignore for now
        }
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
        valueReg = getRegister();
        String data;
        if (atom.getType() instanceof IntegralType) {
            if (atom.isIdent()) {
                try {
                    int index = atom.getContextObjectType().getIndexOf(atom.getData());
                    data = "[" + r.BP + " - (" + index * r.sizeOfInt() + ")]";
                } catch (PropertyNotFoundException e) {
                    throw new RuntimeException("this should not have happened!");
                }
            } else {
                data = atom.getData();
            }
            if (!valueReg.equals(STACK)) {
                program.addInstruction(Opcode.MOV).op(valueReg).op(data).comment(atom.toString());
            } else {
                program.addInstruction(Opcode.PUSH).op(data).comment(atom.toString());
            }
        } else if (atom.getType() instanceof FunctionType) {
            try {
                int index = atom.getContextObjectType().getIndexOf(atom.getData());
                data = "[" + r.BP + " - (" + index * r.sizeOfInt() + ")]";
            } catch (PropertyNotFoundException e) {
                throw new RuntimeException("this should not have happened!");
            }
            if (!valueReg.equals(STACK)) {
                program.addInstruction(Opcode.MOV).op(valueReg).op(data).comment(atom.toString());
            } else {
                program.addInstruction(Opcode.MOV).op(r.DX).op(data).comment(atom.toString());
                program.addInstruction(Opcode.PUSH).op(r.DX).comment(atom.toString());
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
        boolean deepSpilled = deepReg.equals(STACK), shallowSpilled = shallowReg.equals(STACK);
        if (deepSpilled) {
            deepReg = spillOneRegisterOtherThan(deepReg, shallowReg);
        }
        if (shallowSpilled) {
            shallowReg = spillOneRegisterOtherThan(deepReg, shallowReg);
        }
        if (deep == binaryExpression.getLeft()) {
            valueReg = deepReg;
            if (!shallowSpilled) {
                freeRegister(shallowReg);
            }
            consumer.accept(valueReg, shallowReg);
        } else {
            valueReg = shallowReg;
            if (!deepSpilled) {
                freeRegister(deepReg);
            }
            consumer.accept(valueReg, deepReg);
        }
        if (deepSpilled || shallowSpilled) {
            program.addInstruction(Opcode.MOV).op(r.DX).op(valueReg).comment("result temporarily in dx");
            valueReg = STACK;
        }
        if (deepSpilled) {
            restoreFromStack(deepReg);
        }
        if (shallowSpilled) {
            restoreFromStack(shallowReg);
        }
        if (deepSpilled || shallowSpilled) {
            program.addInstruction(Opcode.PUSH).op(r.DX).comment("result goes back to stack");
            valueReg = STACK;
        }
    }

    private void restoreFromStack(String reg) {
        program.addInstruction(Opcode.POP).op(reg).comment("restore " + reg);
    }

    private String getRegister() {
        Optional<String> freeRegOptional = registerAvailabilityMap.entrySet().stream().filter(Entry::getValue).map(Entry::getKey)
            .findFirst();
        freeRegOptional.ifPresent(s -> registerAvailabilityMap.put(s, false));
        return freeRegOptional.orElseGet(() -> STACK);
    }

    private void freeRegister(String reg) {
        registerAvailabilityMap.put(reg, true);
    }

    private String spillOneRegisterOtherThan(String save1, String save2) {
        List<String> resultingRegisters = registerAvailabilityMap.keySet()
            .stream().filter(r -> !r.equals(save1) && !r.equals(save2)).collect(Collectors.toList());
        String reg = resultingRegisters.get(0); // we have 3 registers so this always have a value
        spillOneRegister(reg);
        return reg;
    }

    private void spillOneRegister(String reg) {
        // pop dx ; dx is value
        // push r ; save r
        // moc r, dx
        program.addInstruction(Opcode.POP).op(r.DX).comment("value temporarily in dx");
        program.addInstruction(Opcode.PUSH).op(reg).comment("spill " + reg + "");
        program.addInstruction(Opcode.MOV).op(reg).op(r.DX).comment("value from dx to " + reg);
    }
}
