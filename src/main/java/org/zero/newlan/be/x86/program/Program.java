package org.zero.newlan.be.x86.program;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Program {

    private final List<Instruction> instructions;

    public Program() {
        instructions = new ArrayList<>();
    }

    private Program(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public Instruction addInstruction(Opcode opcode) {
        Instruction ins = new Instruction(opcode);
        this.instructions.add(ins);
        return ins;
    }

    public void addLabel(String label) {
        instructions.add(new Instruction(Opcode.BLANK).label(label));
    }

    public Program simplify() {
        return new Program(pushPopToMov(instructions));
    }

    private List<Instruction> pushPopToMov(List<Instruction> actual) {
        if (actual.isEmpty()) { return new ArrayList<>(actual); }
        List<Instruction> filtered = new ArrayList<>();
        Instruction prev = null;
        for (Instruction current : actual) {
            if (prev != null
                && !current.hasLabel()
                && current.getOpcode() == Opcode.POP
                && prev.getOpcode() == Opcode.PUSH) {
                String op1 = current.opAt(0);
                String op2 = prev.opAt(0);
                if (!op1.equals(op2)) {
                    filtered.set(
                        filtered.size() - 1,
                        new Instruction(Opcode.MOV).op(op1).op(op2).label(prev.label())
                    );
                }
            } else {
                filtered.add(current); // TODO: add a copy instead
            }
            prev = current;
        }
        boolean hasChanged = filtered.size() != actual.size();
        return hasChanged ? pushPopToMov(filtered) : actual;
    }

    @Override
    public String toString() {
        return instructions.stream().map(Instruction::toString).collect(Collectors.joining("\n"));
    }
}
