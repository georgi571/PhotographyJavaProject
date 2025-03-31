package bg.photographyjava.exception;

public class BannedUserException extends RuntimeException {

    public BannedUserException(String message) {
        super(message);
    }
}
