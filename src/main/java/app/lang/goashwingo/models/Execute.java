package app.lang.goashwingo.models;

import app.lang.goashwingo.core.interpreter.VariablePool;
import app.lang.goashwingo.models.TreeModels.BlockStatement;
import app.lang.goashwingo.models.TreeModels.Statement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Execute {
    private BlockStatement blockStatement;
    private VariablePool variablePool;
    private boolean isFunction;

    private int currentOperationId = -1;

    public Execute(BlockStatement blockStatement, boolean isFunction) {
        this.blockStatement = blockStatement;
        this.isFunction = isFunction;
        this.variablePool = new VariablePool();
    }

    public Statement getStatement() {
        if(hasStatement()) {
            return blockStatement.getStatements().get(currentOperationId);
        }
        return null;
    }

    public boolean hasStatement() {
        long functionLength = blockStatement.getStatements().size();
        return this.currentOperationId < functionLength;
    }

    public void stepIn() {
        this.currentOperationId++;
    }

    public void stepBack() {
        this.currentOperationId--;
    }
}
