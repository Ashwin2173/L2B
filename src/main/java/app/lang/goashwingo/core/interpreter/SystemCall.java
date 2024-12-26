package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.exceptions.RunTimeError;
import app.lang.goashwingo.models.TreeModels.CallStatement;
import app.lang.goashwingo.models.TreeModels.ExpressionStatement;

import java.util.List;
import java.util.Scanner;

public class SystemCall {
    public static void run(ExecutionService context, CallStatement callStatement) {
        List<ExpressionStatement> options = callStatement.getParams();
        Scanner scanner = new Scanner(System.in);
        if(options.isEmpty()) throw new InternalError("Called SystemCall with zero Args");

        long operationCode = (Long) new ExpressionService(context).expressionExecutor(options.get(0));
        switch ((int) operationCode) {
            case 1:
                SystemCall.validateOptions(options, 1, 1, callStatement.getLine());
                String outputString = String.valueOf(new ExpressionService(context).expressionExecutor(options.get(1)));
                System.out.print(outputString);
                context.returnValue = null;
                return;
            case 2:
                SystemCall.validateOptions(options, 2, 0, callStatement.getLine());
                context.returnValue = scanner.nextLine();
                return;
            case 10:
                SystemCall.validateOptions(options, 10, 0, callStatement.getLine());
                context.returnValue = System.currentTimeMillis();
                return;
            case 60:
                SystemCall.validateOptions(options, 60, 1, callStatement.getLine());
                Object rawExitCode = new ExpressionService(context).expressionExecutor(options.get(1));
                if(rawExitCode instanceof Long) {
                    System.exit(Math.toIntExact((Long) rawExitCode));
                } else {
                    throw new RunTimeError("System op code '60' required integer as option", callStatement.getLine());
                }
                return;
            default:
                String errorMessage = String.format("Unknown operation code '%s' for system call", operationCode);
                throw new RunTimeError(errorMessage, callStatement.getLine());
        }
    }

    private static void validateOptions(List<ExpressionStatement> options, int opCode, int optionsCount, int line) {
        int listSize = options.size();
        if(listSize != optionsCount + 1) {
            String errorMessage = String.format("System op code '%s' required '%s' options, but got '%s'", opCode, optionsCount, listSize);
            throw new RunTimeError(errorMessage, line);
        }
    }
}
