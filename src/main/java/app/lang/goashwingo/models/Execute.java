package app.lang.goashwingo.models;

import app.lang.goashwingo.core.interpreter.VariablePool;
import app.lang.goashwingo.models.TreeModels.FunctionDeclaration;
import app.lang.goashwingo.models.TreeModels.Statement;
import app.lang.goashwingo.models.TreeModels.VariableDeclaration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Execute {
    private FunctionDeclaration functionDeclaration;
    private VariablePool variablePool;

    private int currentOperationId = -1;

    public Execute(FunctionDeclaration functionDeclaration) {
        this.functionDeclaration = functionDeclaration;
        this.variablePool = new VariablePool();
    }

    public Statement getStatement() {
        if(hasStatement()) {
            return functionDeclaration.getBody().getStatements().get(currentOperationId);
        }
        return null;
    }

    public boolean hasStatement() {
        long functionLength = functionDeclaration.getBody().getStatements().size();
        return this.currentOperationId < functionLength;
    }

    public void stepIn() {
        this.currentOperationId++;
    }

    public void stepBack() {
        this.currentOperationId--;
    }
}
