package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.core.ExpressionType;
import app.lang.goashwingo.core.TokenType;
import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.exceptions.RunTimeError;
import app.lang.goashwingo.models.Execute;
import app.lang.goashwingo.models.Token;
import app.lang.goashwingo.models.TreeModels.*;

import java.util.List;
import java.util.Stack;

public class ExpressionService {
    ExecutionService context;

    public ExpressionService(ExecutionService context) {
        this.context = context;
    }

    public Object expressionExecutor(ExpressionStatement expressionStatement) {
        Expression expression = expressionStatement.getExpression();
        return this.expressionExecutor(expression);
    }

    public Object expressionExecutor(Expression expression) {
        ExpressionType expressionType = expression.getType();
        if(expressionType == ExpressionType.BINARY) {
            return this.executeBinary((BinaryExpression) expression);
        } else if(expressionType == ExpressionType.UNARY) {
            return this.executeUnary((UnaryExpression) expression);
        } else if(expressionType == ExpressionType.ID) {
            return this.readVariable((Identifier) expression, context.getExecutionStack());
        } else if(expressionType == ExpressionType.INT_LITERAL) {
            IntLiteral intLiteral = (IntLiteral) expression;
            return intLiteral.getValue();
        } else if(expressionType == ExpressionType.BOOLEAN_LITERAL) {
            BooleanLiteral booleanLiteral = (BooleanLiteral) expression;
            return booleanLiteral.isValue();
        } else if(expressionType == ExpressionType.RESURRECT_LITERAL) {
            return this.context.returnValue;
        } else if(expressionType == ExpressionType.STRING_LITERAL) {
            StringLiteral stringLiteral = (StringLiteral) expression;
            return stringLiteral.getValue();
        } else if(expressionType == ExpressionType.FUNCTION_CALL) {
            this.handleFunctionCall((CallExpression) expression);
            return this.context.returnValue;
        } else {
            throw new InternalError(String.format("'%s' is not implemented in expressionExecutor", expressionType));
        }
    }

    private void handleFunctionCall(CallExpression callExpression) {
        List<ExpressionStatement> expressionStatementList = callExpression.getArguments();
        String calleeName = callExpression.getCalleeList().get(0).getName();        // todo: fix this shit
        this.context.getOrchestratorContext().callFunction(calleeName, expressionStatementList);
    }

    private Object executeBinary(BinaryExpression expression) {
        Object leftHandSide = expressionExecutor(expression.getLeftExpression());
        Object rightHandSide = expressionExecutor(expression.getRightExpression());
        Token operation = expression.getOperator();
        if(operation.getType() == TokenType.PLUS) {
            return (Long) leftHandSide + (Long) rightHandSide;
        } else if(operation.getType() == TokenType.MINUS) {
            checkBinaryLong(leftHandSide, rightHandSide, operation);
            return (Long) leftHandSide - (Long) rightHandSide;
        } else if (operation.getType() == TokenType.STAR) {
            checkBinaryLong(leftHandSide, rightHandSide, operation);
            return (Long) leftHandSide * (Long) rightHandSide;
        } else if (operation.getType() == TokenType.SLASH) {
            checkBinaryLong(leftHandSide, rightHandSide, operation);
            return (Long) leftHandSide / (Long) rightHandSide;
        } else if(operation.getType() == TokenType.DOUBLE_EQUALS) {
            return leftHandSide == rightHandSide;
        } else if (operation.getType() == TokenType.LESSER_EQUALS) {
            checkBinaryLong(leftHandSide, rightHandSide, operation);
            return (Long) leftHandSide <= (Long) rightHandSide;
        } else if(operation.getType() == TokenType.GREATER_EQUALS) {
            checkBinaryLong(leftHandSide, rightHandSide, operation);
            return (Long) leftHandSide >= (Long) rightHandSide;
        } else if(operation.getType() == TokenType.LESSER) {
            checkBinaryLong(leftHandSide, rightHandSide, operation);
            return (Long) leftHandSide < (Long) rightHandSide;
        } else if (operation.getType() == TokenType.GREATER) {
            checkBinaryLong(leftHandSide, rightHandSide, operation);
            return (Long) leftHandSide > (Long) rightHandSide;
        } else {
            String errorMessage = String.format("'%s' is not implement in expressionExecutor", operation.getType());
            throw new InternalError(errorMessage);
        }
    }

    private void checkBinaryLong(Object leftHandSide, Object rightHandSide, Token token) {
        if(!(leftHandSide instanceof Long && rightHandSide instanceof Long)) {
            String errorMessage = String.format("Invalid Binary operation for '%s'", token.getType());
            throw new RunTimeError(errorMessage, token.getLine());
        }
    }

    private Object executeUnary(UnaryExpression expression) {
        Token operation = expression.getOperator();
        if(operation.getType() == TokenType.MINUS) {
            Object value = this.expressionExecutor(expression.getExpression());
            if(value instanceof Long) {
                return - (Long) this.expressionExecutor(expression.getExpression());
            }
            throw new RunTimeError("Invalid Unary Expression for '-'", operation.getLine());
        } else if(operation.getType() == TokenType.NOT) {
            Object value = this.expressionExecutor(expression.getExpression());
            if(value instanceof Boolean) {
                return !(Boolean) this.expressionExecutor(expression.getExpression());
            }
            return value == null;
        } else {
            String errorMessage = String.format("'%s' unary operation is not implemented", operation.getType());
            throw new InternalError(errorMessage);
        }
    }

    private Object readVariable(Identifier identifier, Stack<Execute> executionStack) {
        String name = identifier.getName();
        int stackSize = executionStack.size() - 1;
        boolean shouldStop = false;
        for(int index = stackSize; index >= 0; index--) {
            Execute execute = executionStack.get(index);
            if(execute.isFunction()) shouldStop = true;
            VariablePool variablePool = execute.getVariablePool();
            Object value = variablePool.get(name);
            if(value != null) {
                return value;
            }
            if(shouldStop) break;
        }
        String errorMessage = String.format("Variable '%s' is not defined", name);
        throw new RunTimeError(errorMessage, identifier.getLine());
    }

    public void writeVariable(Token token, Object newValue, Stack<Execute> executionStack) {
        String name = token.getRaw();
        int stackSize = executionStack.size() - 1;
        boolean shouldStop = false;
        for(int index = stackSize; index >= 0; index--) {
            Execute execute = executionStack.get(index);
            if(execute.isFunction()) shouldStop = true;
            VariablePool variablePool = execute.getVariablePool();
            Object value = variablePool.get(name);
            if(value != null) {
                variablePool.set(token, newValue);
                return;
            }
            if(shouldStop) break;
        }
        String errorMessage = String.format("Variable '%s' is not defined", name);
        throw new RunTimeError(errorMessage, token.getLine());
    }
}
