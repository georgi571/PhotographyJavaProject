package bg.photographyjava.entity.dto;

import java.io.Serializable;
import java.util.List;

public class RegisterInfoDTO implements Serializable {
    private List<String> countries;

    public RegisterInfoDTO(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }
}
