package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.core.StatementType;
import app.lang.goashwingo.core.TokenType;
import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.models.Execute;
import app.lang.goashwingo.models.Token;
import app.lang.goashwingo.models.TreeModels.*;
import lombok.Getter;

import java.util.Stack;

public class ExecutionService {
    @Getter
    private final Stack<Execute> executionStack;
    private final FunctionBuffer functionBuffer;
    public Object returnValue;

    public ExecutionService(FunctionBuffer functionBuffer) {
        this.executionStack = new Stack<>();
        this.functionBuffer = functionBuffer;
        this.callFunction("main");
    }

    public boolean canStepIn() {
        return !this.executionStack.isEmpty();
    }

    public void stepIn() {
        Statement currentStatement = this.getStatement();
        if(currentStatement == null) {
            this.executionStack.pop();
            return;
        }
        StatementType statementType = currentStatement.getType();

        if(statementType == StatementType.RETURN) {
            this.handleReturn((ReturnStatement) currentStatement);
        } else if(statementType == StatementType.CALL) {
            SystemCall.run(this, (CallStatement) currentStatement);
        } else if(statementType == StatementType.VAR_DECLARATION) {
            this.createVariable((VariableDeclaration) currentStatement);
        } else if(statementType == StatementType.EXPRESSION) {
            this.handleExpression((ExpressionStatement) currentStatement);
        } else if(statementType == StatementType.WHILE) {
            this.handleWhile((WhileStatement) currentStatement);
        } else if(statementType == StatementType.ASSIGNMENT) {
            this.handleAssignment((AssignmentStatement) currentStatement);
        } else {
            String errorMessage = String.format("'%s' is not implemented in ExecutionService", currentStatement.getType());
            throw new InternalError(errorMessage);
        }
    }

    private void handleAssignment(AssignmentStatement assignmentStatement) {
        Token name = assignmentStatement.getName();
        ExpressionStatement expressionStatement = assignmentStatement.getExpression();
        Object value = new ExpressionService(this).expressionExecutor(expressionStatement);
        new ExpressionService(this).writeVariable(name, value, executionStack);
    }

    private void handleExpression(ExpressionStatement expressionStatement) {
        new ExpressionService(this).expressionExecutor(expressionStatement);
    }

    private void handleWhile(WhileStatement whileStatement) {
        ExpressionStatement expressionStatement = whileStatement.getExpressionStatement();
        boolean value = (boolean) new ExpressionService(this).expressionExecutor(expressionStatement);
        if(value) {
            String blockName = String.format("while_%s", whileStatement.getLine());
            FunctionDeclaration functionDeclaration = new FunctionDeclaration(new Token(blockName, TokenType.ID, whileStatement.getLine(), null));
            functionDeclaration.setBody(whileStatement.getBody());
            Execute execute = new Execute(functionDeclaration);
            this.executionStack.peek().stepBack();
            this.executionStack.push(execute);
        }
    }

    private void handleReturn(ReturnStatement returnStatement) {
        this.returnValue = new ExpressionService(this).expressionExecutor(returnStatement.getExpression());
        executionStack.pop();
    }

    private void createVariable(VariableDeclaration variableDeclaration) {
        Token name = variableDeclaration.getVariable();
        Object value = new ExpressionService(this).expressionExecutor(variableDeclaration.getInit());
        executionStack.peek().getVariablePool().add(name, value);
    }

    private Statement getStatement() {
        Execute execute = this.executionStack.peek();
        if (execute.hasStatement()) {
            execute.stepIn();
            return execute.getStatement();
        }
        return null;
    }

    private void callFunction(String name) {
        FunctionDeclaration functionDeclaration = functionBuffer.get(name);
        Execute newExecute = new Execute(functionDeclaration);
        this.executionStack.push(newExecute);
    }
}
