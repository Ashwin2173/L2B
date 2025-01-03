package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.ExpressionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringLiteral extends Expression {
    String value;

    public StringLiteral(String value) {
        super.type = ExpressionType.STRING_LITERAL;
        this.value = value;
    }
}