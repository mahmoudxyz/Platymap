package dsl;

public class MappingExecutionException extends RuntimeException {
    public MappingExecutionException(String message) {
        super(message);
    }

    public MappingExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
