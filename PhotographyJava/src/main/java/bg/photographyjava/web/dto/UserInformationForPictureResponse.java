package bg.photographyjava.web.dto;

import java.io.Serializable;
import java.util.UUID;

public class UserInformationForPictureResponse implements Serializable {
    private UUID id;

    private String username;

    private String profilePicturePath;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
