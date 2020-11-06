package org.zero.newlan.fe.ast;

import generated.newlanParser.BodyContext;
import java.util.List;
import java.util.stream.Collectors;
import org.zero.newlan.fe.type.ObjectType;

public class Body extends AstNode {

    private final List<Statement> statements;

    private Body(ObjectType thisObj, List<Statement> statements, String fileName, int lineNumber, int pos) {
        super(thisObj, fileName, lineNumber, pos);
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public static Body from(BodyContext body, ObjectType thisObj, String fileName) {
        return new Body(
            thisObj,
            body.statement().stream().map(s -> Statement.from(s, thisObj, fileName)).collect(Collectors.toList()),
            fileName,
            body.start.getLine(),
            body.start.getCharPositionInLine()
        );
    }
}
