package bg.leaderboards.web.dto;

import bg.leaderboards.leaderboard.model.CountryEnum;

import java.io.Serializable;
import java.util.UUID;

public class LeaderboardsLastThirtyDaysResponse implements Serializable {

    private UUID userId;

    private CountryEnum country;

    private long points;

    private int rank;

    public LeaderboardsLastThirtyDaysResponse() {
    }

    public LeaderboardsLastThirtyDaysResponse(UUID userId, CountryEnum country, long points) {
        this.userId = userId;
        this.country = country;
        this.points = points;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public CountryEnum getCountry() {
        return country;
    }

    public void setCountry(CountryEnum country) {
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
