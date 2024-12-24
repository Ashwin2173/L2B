package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.ExpressionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntLiteral extends Expression {
    long value;

    public IntLiteral(long value) {
        super.type = ExpressionType.INT_LITERAL;
        this.value = value;
    }
}
