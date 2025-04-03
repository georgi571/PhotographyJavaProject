package bg.photographyjava.exception;

public class BlockedUserException extends RuntimeException {

    public BlockedUserException(String message) {
        super(message);
    }
}
