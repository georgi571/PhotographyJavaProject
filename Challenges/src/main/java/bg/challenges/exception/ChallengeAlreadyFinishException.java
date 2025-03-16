package bg.challenges.exception;

public class ChallengeAlreadyFinishException extends RuntimeException {
    public ChallengeAlreadyFinishException(String message) {
        super(message);
    }
}
