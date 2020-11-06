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
        program.addLabel("new_lan_main");
        allocGlobal();
        body.getStatements().forEach(s-> {
            if (s.getData() instanceof Expression) {
                this.compileExpression((Expression) s.getData());
            }
        });
        program.addInstruction(Opcode.LEAVE);
        program.addInstruction(Opcode.RET);
        String outputAsm = "global new_lan_main\n\n" + program.toString();
        Files.write(new File(fileName).toPath(), outputAsm.getBytes());
    }

    private void compileExpression(Expression expression) {
        expressionCompiler.compileExpression(expression);
    }

    private void allocGlobal() {
        try {
            int propertiesSize = TypeRepo.<ObjectType>getType("$Global").getProperties().size() * r.sizeOfInt();
            program.addInstruction(Opcode.PUSH).op(r.BP);
            program.addInstruction(Opcode.MOV).op(r.BP).op(r.SP);
            program.addInstruction(Opcode.ADD).op(r.SP).op(propertiesSize + "");
        } catch (TypeNotFoundException e) {
            // this never happens, ignore
        }
    }
}
