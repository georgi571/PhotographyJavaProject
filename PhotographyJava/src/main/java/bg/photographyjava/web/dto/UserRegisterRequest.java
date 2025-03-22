package bg.photographyjava.web.dto;

import bg.photographyjava.shared.util.validation.annotation.PasswordMatches;
import bg.photographyjava.shared.util.validation.annotation.UniqueEmail;
import bg.photographyjava.shared.util.validation.annotation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

@PasswordMatches
public class UserRegisterRequest implements Serializable {

    @NotEmpty(message = "{username.empty}")
    @UniqueUsername
    @Size(min = 5, max = 20, message = "{username.length}")
    private String username;

    @NotEmpty(message = "{email.empty}")
    @UniqueEmail
    @Email
    private String email;

    @NotEmpty(message = "{password.empty}")
    @Size(min = 6, max = 20, message = "{password.length}")
    private String password;

    @NotEmpty(message = "{confirm.password.empty}")
    @Size(min = 6, max = 20, message = "{password.length}")
    private String confirmPassword;

    @NotEmpty(message = "{country.empty}")
    private String country;

    @NotEmpty(message = "{city.empty}")
    private String city;

    @NotEmpty(message = "{gender.empty}")
    private String gender;

    @NotNull(message = "{age.empty}")
    private LocalDate birthDate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
