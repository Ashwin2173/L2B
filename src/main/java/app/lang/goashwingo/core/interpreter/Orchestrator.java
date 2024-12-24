package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.core.StatementType;
import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.models.TreeModels.FunctionDeclaration;
import app.lang.goashwingo.models.TreeModels.Program;
import app.lang.goashwingo.models.TreeModels.Statement;

public class Orchestrator {
    private final ExecutionService executionService;
    private FunctionBuffer functionBuffer;

    public Orchestrator(Program program) {
        this.initFunctionBuffer(program);
        this.executionService = new ExecutionService(functionBuffer);
    }

    public void start() {
        while(executionService.canStepIn()) {
            executionService.stepIn();
        }
        // System.out.printf("Main function exited with return value of %s\n", executionService.returnValue);
    }

    private void initFunctionBuffer(Program program) {
        this.functionBuffer = new FunctionBuffer();
        for (Statement statement : program.getBody()) {
            if(statement.getType() == StatementType.FUNCTION) {
                FunctionDeclaration functionDeclaration = (FunctionDeclaration) statement;
                this.functionBuffer.add(functionDeclaration.getName().getRaw(), functionDeclaration);
            } else {
                String errorMessage = String.format("'%s' is not implemented in initFunctionBuffer", statement);
                throw new InternalError(errorMessage);
            }
        }
    }
}
