package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CallStatement extends Statement {
    private List<ExpressionStatement> params;
    private int line;

    public CallStatement(List<ExpressionStatement> params, int line) {
        super.type = StatementType.CALL;
        this.params = params;
        this.line = line;
    }
}
