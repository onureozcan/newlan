package org.zero.newlan.fe;

import generated.newlanLexer;
import generated.newlanParser;
import generated.newlanParser.RootContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.zero.newlan.fe.ast.expression.Expression;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new RuntimeException("Expected exactly 1 argument");
        }
        String fileName = args[0];
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
        root.expression().forEach(
            e-> System.out.println(Expression.from(e, fileName))
        );
    }
}
