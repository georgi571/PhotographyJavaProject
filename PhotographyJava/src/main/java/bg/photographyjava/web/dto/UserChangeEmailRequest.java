package bg.photographyjava.web.dto;

import bg.photographyjava.shared.util.validation.annotation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class UserChangeEmailRequest implements Serializable {

    @NotNull
    private String oldEmail;

    @NotNull
    @Email
    @UniqueEmail
    private String newEmail;

    @NotNull
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
