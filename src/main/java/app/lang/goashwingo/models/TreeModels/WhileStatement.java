package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WhileStatement extends Statement {
    private int line;
    private ExpressionStatement expressionStatement;
    private BlockStatement body;

    public WhileStatement(int line) {
        super.type = StatementType.WHILE;
        this.line = line;
    }
}
