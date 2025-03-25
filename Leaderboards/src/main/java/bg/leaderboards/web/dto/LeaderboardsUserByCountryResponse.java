package bg.leaderboards.web.dto;

import java.io.Serializable;
import java.util.UUID;

public class LeaderboardsUserByCountryResponse implements Serializable {

    private UUID userId;

    private String country;

    private long points;

    private int rank;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
