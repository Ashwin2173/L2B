package app.lang.goashwingo.exceptions;

public class UnImplementedBlockError extends RuntimeException {
    public UnImplementedBlockError(String blockName) {
        super(String.format("'%s' is not implemented yet!", blockName));
    }
}
