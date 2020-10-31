package org.zero.newlan.be.x86;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import org.zero.newlan.be.x86.expression.ExpressionCompiler;
import org.zero.newlan.fe.ast.expression.Expression;

public class CompilerX86 {

    public void compile(List<Expression> expressions, String fileName) throws IOException {
        String outputAsm = "newLang_main:\n" +
            expressions.stream().map(this::compileExpression).collect(Collectors.joining(System.lineSeparator()))
            + "pop eax\n"
            + "ret";
        Files.write(new File(fileName + ".asm").toPath(), outputAsm.getBytes());
    }

    private String compileExpression(Expression expression) {
        return ExpressionCompiler.compile(expression);
    }
}
