package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.ExpressionType;
import app.lang.goashwingo.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnaryExpression extends Expression {
    Token operator;
    Expression expression;

    public UnaryExpression(Token operator, Expression expression) {
        super.type = ExpressionType.UNARY;
        this.operator = operator;
        this.expression = expression;
    }
}
