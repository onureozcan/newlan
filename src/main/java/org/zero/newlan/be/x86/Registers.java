package org.zero.newlan.be.x86;

public class Registers {

    public final String AX,BX,CX,DX,SP;
    private final int sizeOfInt;

    public Registers(boolean x64) {
        if (x64) {
            sizeOfInt = 8;
            AX = "rax";
            BX = "rbx";
            CX = "rcx";
            DX = "rdx";
            SP = "rsp";
        } else {
            sizeOfInt = 4;
            AX = "eax";
            BX = "ebx";
            CX = "ecx";
            DX = "edx";
            SP = "esp";
        }
    }

    public int sizeOfInt() {
        return sizeOfInt;
    }
}
