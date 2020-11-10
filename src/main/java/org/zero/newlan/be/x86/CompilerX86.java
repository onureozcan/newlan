package org.zero.newlan.be.x86;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.zero.newlan.be.x86.expression.ExpressionCompiler;
import org.zero.newlan.be.x86.expression.RegisterBasedExpressionCompiler;
import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.Body;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.type.ObjectType;
import org.zero.newlan.fe.type.PropertyNotFoundException;
import org.zero.newlan.fe.type.TypeNotFoundException;
import org.zero.newlan.fe.type.TypeRepo;

public class CompilerX86 {

    private Program program = new Program();

    private Registers r;

    private ExpressionCompiler expressionCompiler;

    public CompilerX86(boolean x64) {
        this.r = new Registers(x64);
        expressionCompiler = new RegisterBasedExpressionCompiler(program, r);
    }

    public void compile(Body body, String fileName) throws IOException {
        program.addHeader("global new_lan_main");
        program.addHeader("global arg0");
        program.addHeader("extern printNumber");
        program.addLabel("new_lan_main");
        prepareGlobal();
        body.getStatements().forEach(s -> {
            if (s.getData() instanceof Expression) {
                this.compileExpression((Expression) s.getData());
            }
        });
        program.addInstruction(Opcode.LEAVE);
        program.addInstruction(Opcode.RET);
        String outputAsm = program.toString() +
            "\n" + getNativeArg0Function().toString();
        Files.write(new File(fileName).toPath(), outputAsm.getBytes());
    }

    private void compileExpression(Expression expression) {
        expressionCompiler.compileExpression(expression);
    }

    private void prepareGlobal() {
        try {
            ObjectType global = TypeRepo.<ObjectType>getType("$Global");
            int propertiesSize = global.getProperties().size() * r.sizeOfInt();
            program.addInstruction(Opcode.PUSH).op(r.BP);
            program.addInstruction(Opcode.MOV).op(r.BP).op(r.SP);
            program.addInstruction(Opcode.SUB).op(r.SP).op(propertiesSize + "");
            initGlobalFunction(global, "printNumber");
        } catch (TypeNotFoundException | PropertyNotFoundException e) {
            // this never happens, wea already validated. yes, it is a design issue, ignore
        }
    }

    private void initGlobalFunction(ObjectType global, String function) throws PropertyNotFoundException {
        int index = global.getIndexOf(function);
        program.addInstruction(Opcode.MOV).op(r.DWORD + " [" + r.BP + " - (" + index * r.sizeOfInt() + ")]").op(function)
            .comment("initialize native function " + function);
    }

    private Program getNativeArg0Function() {
        Program pop = new Program();
        pop.addLabel("arg0");
        pop.addInstruction(Opcode.MOV).op(r.AX).op("[" + r.BP + " + " + r.sizeOfInt() * 2 + " ]");
        pop.addInstruction(Opcode.RET);
        return pop;
    }
}
