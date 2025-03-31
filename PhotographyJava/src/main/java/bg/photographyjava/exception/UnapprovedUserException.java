package bg.photographyjava.exception;

public class UnapprovedUserException extends RuntimeException {

    public UnapprovedUserException(String message) {
        super(message);
    }
}
