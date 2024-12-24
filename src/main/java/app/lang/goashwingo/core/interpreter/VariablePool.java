package app.lang.goashwingo.core.interpreter;

import app.lang.goashwingo.exceptions.RunTimeError;
import app.lang.goashwingo.models.Token;

import java.util.HashMap;

public class VariablePool {
    private final HashMap<String, Object> table;

    public VariablePool() {
        this.table = new HashMap<>();
    }

    public Object get(String name) {
        if(table.containsKey(name)) {
            return this.table.get(name);
        }
        return null;
    }

    public void add(Token token, Object value) {
        String name = token.getRaw();
        if(table.containsKey(name)) {
            String errorMessage = String.format("Variable '%s' is already declared in the scope", name);
            throw new RunTimeError(errorMessage, token.getLine());
        }
        this.table.put(name, value);
    }

    public void set(Token token, Object value) {
        this.table.put(token.getRaw(), value);
    }
}
