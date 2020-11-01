package org.zero.newlan.fe;

import generated.newlanLexer;
import generated.newlanParser;
import generated.newlanParser.RootContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.zero.newlan.be.x86.CompilerX86;
import org.zero.newlan.fe.ast.expression.Expression;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new RuntimeException("Expected exactly 2 arguments");
        }
        String fileName = args[0];
        String outputFileName = args[1];
        newlanLexer lexer = new newlanLexer(
            CharStreams.fromString(
                String.join(System.lineSeparator(), Files.readAllLines(
                    new File(fileName).toPath()
                    )
                )
            )
        );
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        newlanParser parser = new newlanParser(tokens);
        RootContext root = parser.root();
        List<Expression> expressionList = root.expression().stream().map(
            e-> Expression.from(e, fileName)
        ).collect(Collectors.toList());
        expressionList.forEach(System.out::println);
        CompilerX86 compiler = new CompilerX86();
        compiler.compile(expressionList, outputFileName);
    }
}
