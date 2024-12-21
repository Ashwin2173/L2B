package app.lang.goashwingo.models.TreeModels;

public class ReturnStatement extends Statement {
    ExpressionStatement expression;

    public ReturnStatement(ExpressionStatement expression) {
        super.type = this.getClass().getName();
        this.expression = expression;
    }
}
