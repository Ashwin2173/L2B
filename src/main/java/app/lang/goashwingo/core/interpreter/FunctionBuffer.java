package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.models.TreeModels.FunctionDeclaration;

import java.util.HashMap;

public class FunctionBuffer {
    HashMap<String, FunctionDeclaration> functions;

    public FunctionBuffer() {
        this.functions = new HashMap<>();
    }

    public void add(String name, FunctionDeclaration functionDeclaration) {
        this.functions.put(name, functionDeclaration);
    }

    public FunctionDeclaration get(String name) {
        if(this.functions.containsKey(name)) {
            return this.functions.get(name);
        }
        String errorMessage = String.format("'%s' function called before declaration", name);
        throw new InternalError(errorMessage); // todo: fix this with LoomRunTimeError
    }
}
