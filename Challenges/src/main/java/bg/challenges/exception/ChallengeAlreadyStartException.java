package bg.challenges.exception;

public class ChallengeAlreadyStartException extends RuntimeException {
    public ChallengeAlreadyStartException(String message) {
        super(message);
    }
}
