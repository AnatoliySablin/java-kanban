package exceptions;

public class ManagerValidationException extends RuntimeException {
    public ManagerValidationException(String message) {
        super(message);
    }
}