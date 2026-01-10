package io.github.ashwithpoojary98;

/**
 * Thrown when a BiDi command fails.
 */
public class BiDiError extends RuntimeException {

    private final String error;
    private final String errorMessage;

    public BiDiError(String error, String message) {
        super(error + ": " + message);
        this.error = error;
        this.errorMessage = message;
    }

    /**
     * Get the error code.
     */
    public String getError() {
        return error;
    }

    /**
     * Get the error message from the server.
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
