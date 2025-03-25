package bg.leaderboards.web.dto;

import bg.leaderboards.leaderboard.model.CountryEnum;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class UserRegisterV1 implements Serializable {
    @NotNull
    private UUID userId;

    @NotNull
    private CountryEnum country;

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
}
