package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import app.lang.goashwingo.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableDeclaration extends Statement {
    Token variable;
    ExpressionStatement init;

    public VariableDeclaration(Token variableName, ExpressionStatement init) {
        super.type = StatementType.VAR_DECLARATION;
        this.variable = variableName;
        this.init = init;
    }
}
