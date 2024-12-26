package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IfStatement extends Statement {
    private int line;
    private ExpressionStatement expressionStatement;
    private BlockStatement ifBlock;
    private BlockStatement elseBlock;

    public IfStatement(int line) {
        super.type = StatementType.IF;
        this.line = line;
    }
}
