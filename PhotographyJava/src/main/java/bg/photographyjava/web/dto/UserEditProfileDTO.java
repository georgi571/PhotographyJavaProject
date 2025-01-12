package bg.photographyjava.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class UserEditProfileDTO implements Serializable {

    @NotEmpty(message = "{real.name.empty}")
    @Size(min = 2, max = 50, message = "{real.name.length}")
    private String realName;

    @NotEmpty(message = "{city.empty}")
    private String city;

    @NotNull(message = "{age.empty}")
    private int age;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
