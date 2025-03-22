package bg.photographyjava.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class UserChangePasswordRequest implements Serializable {

    @NotNull
    private String oldPassword;

    @NotNull
    @Size(min = 6, max = 20, message = "{password.length}")
    private String newPassword;

    @NotNull
    @Size(min = 6, max = 20, message = "{password.length}")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
