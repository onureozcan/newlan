package org.zero.newlan;

import generated.newlanLexer;
import generated.newlanParser;
import generated.newlanParser.RootContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.zero.newlan.be.x86.CompilerX86;
import org.zero.newlan.fe.ast.Body;
import org.zero.newlan.fe.ast.expression.Expression;
import org.zero.newlan.fe.type.TypeNotFoundException;
import org.zero.newlan.fe.type.TypeRepo;

public class Main {

    public static void main(String[] args) throws IOException, TypeNotFoundException {
        if (args.length < 2) {
            throw new RuntimeException("Expected at least 2 arguments");
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
        Body body = Body.from(root.body(), TypeRepo.getType("$Global"), fileName);
        CompilerX86 compiler = new CompilerX86(Arrays.asList(args).contains("-x64"));
        compiler.compile(body, outputFileName);
    }
}
