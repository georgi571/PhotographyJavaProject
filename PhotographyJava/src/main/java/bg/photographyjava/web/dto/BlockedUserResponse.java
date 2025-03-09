package bg.photographyjava.web.dto;

import java.io.Serializable;

public class BlockedUserResponse implements Serializable {
    private String username;
    private String profilePicturePath;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
}
