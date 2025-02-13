package bg.photographyjava.web.dto;

import java.io.Serializable;
import java.util.List;

public class RegisterInfoResponse implements Serializable {
    private List<String> countries;

    public RegisterInfoResponse(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }
}
