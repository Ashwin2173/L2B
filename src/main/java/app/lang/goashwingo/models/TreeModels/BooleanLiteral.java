package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.ExpressionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BooleanLiteral extends Expression {
    boolean value;

    public BooleanLiteral(boolean value) {
        super.type = ExpressionType.BOOLEAN_LITERAL;
        this.value = value;
    }
}
