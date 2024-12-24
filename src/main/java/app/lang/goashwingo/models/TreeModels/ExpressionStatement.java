package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpressionStatement extends Statement {
    Expression expression;

    public ExpressionStatement(Expression expression) {
        super.type = StatementType.EXPRESSION;
        this.expression = expression;
    }
}
