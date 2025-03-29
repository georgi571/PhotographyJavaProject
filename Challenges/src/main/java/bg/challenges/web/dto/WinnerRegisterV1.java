package bg.challenges.web.dto;

import bg.challenges.challenge.model.ChallengeType;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class WinnerRegisterV1 implements Serializable {

    @NotNull
    private UUID userId;

    @NotNull
    private ChallengeType type;

    @NotNull
    private int points;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public ChallengeType getType() {
        return type;
    }

    public void setType(ChallengeType type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
