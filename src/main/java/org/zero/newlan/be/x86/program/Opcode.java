package org.zero.newlan.be.x86.program;

public enum Opcode {
    BLANK(""),
    NOP("nop"),
    PUSH("push"),
    POP("pop"),
    ADD("add"),
    SUB("sub"),
    IMUL("imul"),
    IDIV("idiv"),
    RET("ret");

    private String value;

    Opcode(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
