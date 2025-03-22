package bg.photographyjava.web.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class UserEditProfileResponse implements Serializable {

    private String realName;

    private String city;

    private LocalDate birthDate;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
