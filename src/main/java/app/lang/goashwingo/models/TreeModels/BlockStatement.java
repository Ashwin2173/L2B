package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.core.StatementType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlockStatement extends Statement {
    List<Statement> statements;
    int line;

    public BlockStatement(List<Statement> statements, int line) {
        super.type = StatementType.BLOCK;
        this.statements = statements;
        this.line = line;
    }
}
