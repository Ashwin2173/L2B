package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.core.StatementType;
import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.models.Execute;
import app.lang.goashwingo.models.TreeModels.*;

import java.util.ArrayList;
import java.util.List;

public class Orchestrator {
    private final ExecutionService executionService;
    private FunctionBuffer functionBuffer;

    public Orchestrator(Program program) {
        this.initFunctionBuffer(program);
        this.executionService = new ExecutionService(this);
    }

    public void start() {
        callFunction("main", new ArrayList<>());
        // System.out.printf("Main function exited with return value of %s\n", executionService.returnValue);
    }

    public void callFunction(String name, List<ExpressionStatement> arguments) {
        FunctionDeclaration functionDeclaration = functionBuffer.get(name);
        Execute newExecute = new Execute(functionDeclaration.getBody(), true);
        VariablePool variablePool = executionService.initFunctionCall(name, functionDeclaration.getArgumentsName(), arguments, functionDeclaration.getName().getLine());
        newExecute.setVariablePool(variablePool);
        executionService.getExecutionStack().push(newExecute);
        while(executionService.canStepIn()) {
            executionService.stepIn();
            if(executionService.isReturned()) {
                executionService.resetReturn();
                return ;
            }
        }
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
