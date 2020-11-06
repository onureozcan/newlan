package org.zero.newlan.be.x86;

public class Registers {

    public final String AX,BX,CX,DX,SP,BP;
    private final int sizeOfInt;

    public Registers(boolean x64) {
        if (x64) {
            sizeOfInt = 8;
            AX = "rax";
            BX = "rbx";
            CX = "rcx";
            DX = "rdx";
            SP = "rsp";
            BP = "rbp";
        } else {
            sizeOfInt = 4;
            AX = "eax";
            BX = "ebx";
            CX = "ecx";
            DX = "edx";
            SP = "esp";
            BP = "rbp";
        }
    }

    public int sizeOfInt() {
        return sizeOfInt;
    }
}
