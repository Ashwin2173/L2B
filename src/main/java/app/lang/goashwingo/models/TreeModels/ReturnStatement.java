package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import lombok.Getter;

@Getter
public class ReturnStatement extends Statement {
    ExpressionStatement expression;

    public ReturnStatement(ExpressionStatement expression) {
        super.type = StatementType.RETURN;
        this.expression = expression;
    }
}
