package app.lang.goashwingo.models.TreeModels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Identifier extends Expression {
    String name;

    public Identifier(String name) {
        super.type = this.getClass().getName();
        this.name = name;
    }
}
