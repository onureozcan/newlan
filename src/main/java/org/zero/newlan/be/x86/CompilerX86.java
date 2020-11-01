package org.zero.newlan.be.x86;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.zero.newlan.be.x86.expression.ExpressionCompiler;
import org.zero.newlan.be.x86.program.Opcode;
import org.zero.newlan.be.x86.program.Program;
import org.zero.newlan.fe.ast.expression.Expression;

public class CompilerX86 {

    private Program program = new Program();
    private ExpressionCompiler expressionCompiler = new ExpressionCompiler(program);

    public void compile(List<Expression> expressions, String fileName) throws IOException {
        program.addLabel("new_lang_main");
        expressions.forEach(this::compileExpression);
        program.addInstruction(Opcode.POP).op("eax");
        program.addInstruction(Opcode.RET);
        String outputAsm = program.toString();
        Files.write(new File(fileName + ".asm").toPath(), outputAsm.getBytes());
    }

    private void compileExpression(Expression expression) {
        expressionCompiler.compile(expression);
    }
}
