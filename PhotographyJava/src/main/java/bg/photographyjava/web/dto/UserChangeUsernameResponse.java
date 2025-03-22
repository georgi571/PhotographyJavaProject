package bg.photographyjava.web.dto;

import java.io.Serializable;

public class UserChangeUsernameResponse implements Serializable {

    private String oldUsername;

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }
}
