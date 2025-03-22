package bg.photographyjava.web.dto;

import bg.photographyjava.shared.util.validation.annotation.UniqueUsername;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class UserChangeUsernameRequest implements Serializable {

    @NotNull
    private String oldUsername;

    @Size(min = 2, max = 50)
    @UniqueUsername
    private String newUsername;

    @NotNull
    private String password;

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
