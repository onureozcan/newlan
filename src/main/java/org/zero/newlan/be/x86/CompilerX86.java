package org.zero.newlan.be.x86;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.zero.newlan.be.x86.expression.ExpressionCompiler;
import org.zero.newlan.be.x86.expression.RegisterBasedExpressionCompiler;
import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.Expression;

public class CompilerX86 {

    private Program program = new Program();
    private Registers r;
    private ExpressionCompiler expressionCompiler;

    public CompilerX86(boolean x64) {
        this.r = new Registers(x64);
        expressionCompiler = new RegisterBasedExpressionCompiler(program, r);
    }

    public void compile(List<Expression> expressions, String fileName) throws IOException {
        program.addLabel("new_lan_main");
        expressions.forEach(this::compileExpression);
        program.addInstruction(Opcode.RET);
        String outputAsm = "global new_lan_main\n\n" + program.toString();
        Files.write(new File(fileName).toPath(), outputAsm.getBytes());
    }

    private void compileExpression(Expression expression) {
        expressionCompiler.compileExpression(expression);
    }
}
