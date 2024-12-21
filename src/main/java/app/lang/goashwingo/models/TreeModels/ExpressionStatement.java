package app.lang.goashwingo.models.TreeModels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpressionStatement extends Statement {
    Expression expression;

    public ExpressionStatement(Expression expression) {
        super.type = this.getClass().getName();
        this.expression = expression;
    }
}
