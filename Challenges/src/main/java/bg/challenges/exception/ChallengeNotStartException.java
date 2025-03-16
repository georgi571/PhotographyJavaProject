package bg.challenges.exception;

public class ChallengeNotStartException extends RuntimeException {
    public ChallengeNotStartException(String message) {
        super(message);
    }
}
