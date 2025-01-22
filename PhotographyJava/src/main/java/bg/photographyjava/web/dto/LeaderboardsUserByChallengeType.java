package bg.photographyjava.web.dto;

import java.util.UUID;

public class LeaderboardsUserByChallengeType {
    private UUID id;

    private String username;

    private long numberOfWinChallenges;

    private String challengeType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getNumberOfWinChallenges() {
        return numberOfWinChallenges;
    }

    public void setNumberOfWinChallenges(long numberOfWinChallenges) {
        this.numberOfWinChallenges = numberOfWinChallenges;
    }

    public String getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }
}
