package bg.leaderboards.web.dto;

import java.io.Serializable;

public class LeaderboardsUserByCountryResponse implements Serializable {

    private String username;

    private String country;

    private long points;

    private int rank;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
