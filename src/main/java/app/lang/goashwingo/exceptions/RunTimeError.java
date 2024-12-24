package app.lang.goashwingo.exceptions;

import app.lang.goashwingo.models.Token;
import lombok.Getter;

@Getter
public class RunTimeError extends RuntimeException {
    int line;
    public RunTimeError(String message, int line) {
        super(message);
        this.line = line;
    }
}
