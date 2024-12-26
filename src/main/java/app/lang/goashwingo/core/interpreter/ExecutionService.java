package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.core.StatementType;
import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.exceptions.RunTimeError;
import app.lang.goashwingo.models.Execute;
import app.lang.goashwingo.models.Token;
import app.lang.goashwingo.models.TreeModels.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Stack;

public class ExecutionService {
    @Getter
    private final Stack<Execute> executionStack;
    @Getter
    private final Orchestrator orchestratorContext;
    @Getter
    @Setter
    private boolean returned;
    private final ExpressionService expressionService;
    public Object returnValue;

    public ExecutionService(Orchestrator orchestratorContext) {
        this.orchestratorContext = orchestratorContext;
        this.executionStack = new Stack<>();
        this.returned = false;
        this.expressionService = new ExpressionService(this);
    }

    public boolean canStepIn() {
        return !this.executionStack.isEmpty();
    }

    public void stepIn() {
        Statement currentStatement = this.getStatement();
        if(currentStatement == null) {
            if(this.executionStack.peek().isFunction()) {
                ReturnStatement returnStatement = new ReturnStatement(new ExpressionStatement(new IntLiteral(0)));      // todo: fix this to return null
                this.handleReturn(returnStatement);
                return ;
            } else {
                this.executionStack.pop();
                return;
            }
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
        } else if(statementType == StatementType.IF) {
            this.handleIf((IfStatement) currentStatement);
        } else {
            String errorMessage = String.format("'%s' is not implemented in ExecutionService", currentStatement.getType());
            throw new InternalError(errorMessage);
        }
    }

    private void handleIf(IfStatement ifStatement) {
        ExpressionStatement expressionStatement = ifStatement.getExpressionStatement();
        boolean value = (Boolean) expressionService.expressionExecutor(expressionStatement);
        if(value) {
            Execute execute = new Execute(ifStatement.getIfBlock(), false);
            this.executionStack.push(execute);
        } else if(ifStatement.getElseBlock() != null) {
            Execute execute = new Execute(ifStatement.getElseBlock(), false);
            this.executionStack.push(execute);
        }
    }

    private void handleAssignment(AssignmentStatement assignmentStatement) {
        Token name = assignmentStatement.getName();
        ExpressionStatement expressionStatement = assignmentStatement.getExpression();
        Object value = expressionService.expressionExecutor(expressionStatement);
        expressionService.writeVariable(name, value, executionStack);
    }

    private void handleExpression(ExpressionStatement expressionStatement) {
        expressionService.expressionExecutor(expressionStatement);
    }

    private void handleWhile(WhileStatement whileStatement) {
        ExpressionStatement expressionStatement = whileStatement.getExpressionStatement();
        boolean value = (boolean) expressionService.expressionExecutor(expressionStatement);
        if(value) {
            Execute execute = new Execute(whileStatement.getBody(), false);
            this.executionStack.peek().stepBack();
            this.executionStack.push(execute);
        }
    }

    private void handleReturn(ReturnStatement returnStatement) {
        this.returnValue = expressionService.expressionExecutor(returnStatement.getExpression());
        while(!executionStack.peek().isFunction()) {
            executionStack.pop();
        }
        executionStack.pop();
        returned = true;
    }

    public VariablePool initFunctionCall(String name, List<String> argumentNames, List<ExpressionStatement> arguments, int line) {
        if(argumentNames.size() != arguments.size()) {
            String errorMessage = String.format("'%s' function required %s argument(s) but got %s", name, argumentNames.size(), arguments.size());
            throw new RunTimeError(errorMessage, line);
        }
        VariablePool variablePool = new VariablePool();
        for(int index = 0; index < argumentNames.size(); index++) {
            String argumentName = argumentNames.get(index);
            Object value = expressionService.expressionExecutor(arguments.get(index));
            variablePool.add(argumentName, value, line);
        }
        return variablePool;
    }

    private void createVariable(VariableDeclaration variableDeclaration) {
        Token name = variableDeclaration.getVariable();
        Object value = expressionService.expressionExecutor(variableDeclaration.getInit());
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

    public void resetReturn() {
        this.returned = false;
    }
}
