package bg.leaderboards.web.dto;

import bg.leaderboards.leaderboard.property.CountryEnum;

import java.io.Serializable;

public class LeaderboardsLastThirtyDaysResponse implements Serializable {

    private String username;

    private CountryEnum country;

    private long points;

    private int rank;

    public LeaderboardsLastThirtyDaysResponse() {
    }

    public LeaderboardsLastThirtyDaysResponse(String username, CountryEnum country, long points) {
        this.username = username;
        this.country = country;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
