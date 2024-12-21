package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryExpression extends Expression {
    Expression leftExpression;
    Expression rightExpression;
    Token operator;

    public BinaryExpression(Expression leftExpression, Token operator, Expression rightExpression) {
        super.type = this.getClass().getName();
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }
}
