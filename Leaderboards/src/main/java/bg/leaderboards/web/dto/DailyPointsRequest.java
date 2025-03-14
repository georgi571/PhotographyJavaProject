package bg.leaderboards.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class DailyPointsRequest implements Serializable {

    @NotEmpty
    @Size(min = 5, max = 20)
    private String username;

    @NotEmpty
    private String country;

    @NotEmpty
    private String type;

    @NotEmpty
    private int points;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
