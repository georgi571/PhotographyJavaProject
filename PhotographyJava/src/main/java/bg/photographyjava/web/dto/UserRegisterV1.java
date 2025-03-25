package bg.photographyjava.web.dto;

import bg.photographyjava.user.property.enums.CountryEnum;

import java.io.Serializable;
import java.util.UUID;

public class UserRegisterV1 implements Serializable {

    private UUID userId;

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
