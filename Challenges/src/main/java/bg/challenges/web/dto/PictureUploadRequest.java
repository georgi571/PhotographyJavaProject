package bg.challenges.web.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class PictureUploadRequest implements Serializable {

    @NotNull
    private String caption;

    @NotNull
    private String story;

    @NotNull
    private UUID userId;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
