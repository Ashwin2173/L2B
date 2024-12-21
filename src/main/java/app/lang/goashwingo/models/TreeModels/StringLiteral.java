package app.lang.goashwingo.models.TreeModels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringLiteral extends Expression {
    String value;

    public StringLiteral(String value) {
        super.type = this.getClass().getName();
        this.value = value;
    }
}