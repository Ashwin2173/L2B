package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.ExpressionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Identifier extends Expression {
    String name;
    int line;

    public Identifier(String name, int line) {
        super.type = ExpressionType.ID;
        this.line = line;
        this.name = name;
    }
}
