package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.exceptions.RunTimeError;
import app.lang.goashwingo.models.TreeModels.CallStatement;
import app.lang.goashwingo.models.TreeModels.ExpressionStatement;

import java.util.List;

public class SystemCall {
    public static void run(ExecutionService context, CallStatement callStatement) {
        List<ExpressionStatement> options = callStatement.getParams();
        if(options.isEmpty()) throw new InternalError("Called SystemCall with zero Args");

        long operationCode = (Long) new ExpressionService(context).expressionExecutor(options.get(0));
        switch ((int) operationCode) {
            case 1:
                if(options.size() != 2) throw new InternalError("Operation code 1 requires 2 options");
                String outputString = String.valueOf(new ExpressionService(context).expressionExecutor(options.get(1)));
                System.out.print(outputString);
                return;
            case 10:
                if(options.size() != 1) throw new InternalError("Operation code 10 requires 1 options");
                context.returnValue = System.currentTimeMillis();
                return;
            default:
                String errorMessage = String.format("Unknown operation code '%s' for system call", operationCode);
                throw new RunTimeError(errorMessage, callStatement.getLine());
        }
    }
}
