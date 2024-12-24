package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.ExpressionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CallExpression extends Expression {
    List<Identifier> calleeList;
    List<ExpressionStatement> arguments;
    int line;

    public CallExpression(List<Identifier> calleeList, List<ExpressionStatement> arguments, int line) {
        super.type = ExpressionType.FUNCTION_CALL;
        this.calleeList = calleeList;
        this.arguments = arguments;
        this.line = line;
    }
}
