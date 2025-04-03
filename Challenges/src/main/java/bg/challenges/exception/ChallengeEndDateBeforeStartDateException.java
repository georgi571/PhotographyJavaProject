package bg.challenges.exception;

public class ChallengeEndDateBeforeStartDateException extends RuntimeException {

    public ChallengeEndDateBeforeStartDateException(String message) {
        super(message);
    }
}
