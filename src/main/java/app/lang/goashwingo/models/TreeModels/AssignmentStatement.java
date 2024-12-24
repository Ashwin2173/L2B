package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import app.lang.goashwingo.models.Token;
import lombok.Getter;

@Getter
public class AssignmentStatement extends Statement {
    private final Token name;
    private final ExpressionStatement expression;

    public AssignmentStatement(Token name, ExpressionStatement expressionStatement) {
        super.type = StatementType.ASSIGNMENT;
        this.name = name;
        this.expression = expressionStatement;
    }
}
