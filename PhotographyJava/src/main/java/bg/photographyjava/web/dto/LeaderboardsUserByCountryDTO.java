package bg.photographyjava.web.dto;

import java.util.UUID;

public class LeaderboardsUserByCountryDTO {

    private UUID id;

    private String username;

    private String country;

    private int points;

    private int rank;

    public LeaderboardsUserByCountryDTO(UUID id, String username, String country, int points, int i) {
        this.id = id;
        this.username = username;
        this.country = country;
        this.points = points;
        this.rank = i;
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
