package bg.photographyjava.web.dto;

import java.io.Serializable;

public class UserChangeEmailResponse implements Serializable {

    private String oldEmail;

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }
}
