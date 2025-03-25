package bg.leaderboards.web.dto;

import java.io.Serializable;
import java.util.UUID;

public class LeaderboardsUserByChallengeTypeResponse implements Serializable {

    private UUID userId;

    private long numberOfWinChallenges;

    private String challengeType;

    private String country;

    private int rank;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
