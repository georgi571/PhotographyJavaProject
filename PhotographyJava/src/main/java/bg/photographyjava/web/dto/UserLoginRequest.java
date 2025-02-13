package bg.photographyjava.web.dto;

import bg.photographyjava.shared.util.validation.annotation.ValidUserLogin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@ValidUserLogin
public class UserLoginRequest implements Serializable {

    @NotEmpty(message = "{username.empty}")
    @Size(min = 5, max = 20, message = "{username.length}")
    private String username;

    @NotEmpty(message = "{password.empty}")
    @Size(min = 5, max = 20, message = "{password.length}")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
