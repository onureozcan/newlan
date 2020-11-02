package org.zero.newlan.be.x86;

public class Registers {

    public final String AX,BX,CX,DX;

    public Registers(boolean x64) {
        if (x64) {
            AX = "rax";
            BX = "rbx";
            CX = "rcx";
            DX = "rdx";
        } else {
            AX = "eax";
            BX = "ebx";
            CX = "ecx";
            DX = "edx";
        }
    }
}
