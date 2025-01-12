package bg.photographyjava.web.dto;

import bg.photographyjava.shared.util.validation.annotation.UniqueEmail;
import jakarta.validation.constraints.Email;

import java.io.Serializable;

public class UserChangeEmailDTO implements Serializable {

    private String oldEmail;

    @Email
    @UniqueEmail
    private String newEmail;

    private String password;

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
