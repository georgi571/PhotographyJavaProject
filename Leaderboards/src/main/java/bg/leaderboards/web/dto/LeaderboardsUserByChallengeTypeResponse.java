package bg.leaderboards.web.dto;

import java.io.Serializable;

public class LeaderboardsUserByChallengeTypeResponse implements Serializable {

    private String username;

    private long numberOfWinChallenges;

    private String challengeType;

    private String country;

    private int rank;

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
