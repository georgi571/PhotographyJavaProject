package bg.photographyjava.web.dto;

import java.util.UUID;

public class PictureDTO {
    private UUID id;

    private String imageUrl;

    private UserInformationForPictureDTO user;

    private long likes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UserInformationForPictureDTO getUser() {
        return user;
    }

    public void setUser(UserInformationForPictureDTO user) {
        this.user = user;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }
}
