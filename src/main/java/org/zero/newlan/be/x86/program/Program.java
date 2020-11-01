package org.zero.newlan.be.x86.program;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Program {

    private final List<Instruction> instructions = new ArrayList<>();

    public Instruction addInstruction(Opcode opcode) {
        Instruction ins = new Instruction(opcode);
        this.instructions.add(ins);
        return ins;
    }

    public void addLabel(String label) {
        instructions.add(new Instruction(Opcode.BLANK).label(label));
    }

    @Override
    public String toString() {
        return instructions.stream().map(Instruction::toString).collect(Collectors.joining("\n"));
    }
}
