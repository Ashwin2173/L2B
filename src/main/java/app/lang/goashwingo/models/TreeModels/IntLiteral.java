package app.lang.goashwingo.models.TreeModels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntLiteral extends Expression {
    int value;

    public IntLiteral(int value) {
        super.type = this.getClass().getName();
        this.value = value;
    }
}
