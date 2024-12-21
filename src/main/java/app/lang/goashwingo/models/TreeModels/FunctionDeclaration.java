package app.lang.goashwingo.models.TreeModels;

import app.lang.goashwingo.exceptions.LoomSyntaxError;
import app.lang.goashwingo.models.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class FunctionDeclaration extends Statement {
    Token name;
    List<String> argumentsName = new ArrayList<>();
    BlockStatement body;

    public FunctionDeclaration(Token name) {
        super.type = this.getClass().getName();
        this.name = name;
    }

    public void addArguments(Token token) {
        String name = token.getRaw();
        if(argumentsName.contains(name)) {
            String functionName = this.getName().getRaw();
            String errorMessage = String.format("Duplicate argument '%s' in '%s' function definition", name, functionName);
            throw new LoomSyntaxError(errorMessage);
        }
        argumentsName.add(name);
    }
}
