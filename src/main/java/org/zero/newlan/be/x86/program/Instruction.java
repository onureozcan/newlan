package org.zero.newlan.be.x86.program;

import java.util.ArrayList;
import java.util.List;

public class Instruction {

    private final Opcode opcode;

    private final List<String> operands = new ArrayList<>();

    private String label;

    private String comment = "";

    Instruction(Opcode opcode) {
        this.opcode = opcode;
    }

    Opcode getOpcode() {
        return opcode;
    }

    public List<String> getOperands() {
        return operands;
    }

    Instruction label(String label) {
        this.label = label;
        return this;
    }

    public Instruction comment(String comment) {
        this.comment = comment;
        return this;
    }

    public Instruction op(String operand) {
        this.operands.add(operand);
        return this;
    }

    String opAt(int i) {
        return operands.get(i);
    }

    String label() {
        return label;
    }

    boolean hasLabel() {
        return label != null;
    }

    @Override
    public String toString() {
        return (hasLabel() ? label + ":" : "\t") + opcode + " " + String.join(",", operands) + "; " + comment;
    }
}
